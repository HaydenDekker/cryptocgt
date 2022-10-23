package com.hdekker.cryptocgt.cgt;

import static com.hdekker.cryptocgt.balance.BalanceAssesment.sumCoinOrderBalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.cgt.search.MoreThan12MonthDateSearcher;
import com.hdekker.cryptocgt.cgt.search.MostRecentDateSearcher;
import com.hdekker.cryptocgt.cgt.search.refactor.CGTEventDateRangeSearcher;
import com.hdekker.cryptocgt.cgt.search.refactor.CGTEventDateSearchType;
import com.hdekker.cryptocgt.cgt.search.refactor.SearchQuery;
import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.data.CGTEvent;
import com.hdekker.cryptocgt.data.AssetBalance.BalanceType;
import com.hdekker.cryptocgt.reports.Utils;

@Component
public class CGTCalculator {
	
	private static Function<List<AssetBalance>, List<CGTEvent>> getCGTSForDisposals(Function<AssetBalance, Stream<CGTEvent>> cgtGetter){
		return (disposals) ->{
			
			return disposals.stream()
						.flatMap(d-> {
							
							//log.info("Starting disposal of " + d.getCoinName() + " on " + d.getBalanceDate().toString() + " and amount " + d.getCoinAmount());
							return cgtGetter.apply(d);
						})
						.collect(Collectors.toList());
			
		};
	}
	
	public static BiFunction<AssetBalance, AssetBalance, CGTEvent> createCGTEvent(){
		
		return (cobCurrent, cobPast)-> {
			
			Double purchaseCost = Math.abs(cobCurrent.getAssetAmount())*cobPast.getExchangeRateAUD();
			Double sellPrice = Math.abs(cobCurrent.getAssetAmount())*cobCurrent.getExchangeRateAUD();
			
			CGTEvent e = new CGTEvent(
					cobCurrent.getBalanceDate(),
					cobPast.getBalanceDate(),
					sellPrice-purchaseCost,
					cobCurrent.getAssetName()
					);
			
			return e;
			
		};
		
	}
	
	@Autowired
	MoreThan12MonthDateSearcher moreThan12MonthSearcher;
	
	@Autowired
	MostRecentDateSearcher mostRecentDateSearcher;
	
	public Function<AssetBalance, Stream<CGTEvent>> createCGTEventsForDisposal(List<AssetBalance> balanceList){
		
		// statefull list for as long as the function is used.
		Logger log = LoggerFactory.getLogger(CGTUtils.class);
		
		return (disposal) -> {
			
			Stream<CGTEvent> stream = Stream.empty();
			
			AssetBalance mostRecent = null;
			
			mostRecent = moreThan12MonthSearcher.search(AssetBalance::getBalanceDate)
					.apply(disposal, balanceList)
					.orElse(null);
			
			try {
			// TODO this needs to change to a maximise return function.
		    // Choose item over 12 months if available else choose most recent.
			// That way more purchases have a potential to reach the 12 month discount
			// marker.
			
			if(mostRecent==null) {
				mostRecent = mostRecentDateSearcher.search(AssetBalance::getBalanceDate)
						.apply(disposal, balanceList)
						.orElseThrow();
			}
			}
			catch(Exception e) {

				if(disposal.getAssetAmount()*disposal.getExchangeRateAUD()>1) {
					log.error("This was too big for just a rounding issue");
				}
				
				return Stream.empty();
			}
			AssetBalance newBalanceAtMostRecent = sumCoinOrderBalance().apply(mostRecent, disposal);
				
			// add to stream if new balance positive and get next
			if(newBalanceAtMostRecent.getAssetAmount()<0) {
					
				// remove most recent creating new list
				balanceList.remove(mostRecent);
				
				// create cgt for removal
				stream = Stream.concat(stream, Stream.of(createCGTEvent().apply(disposal, mostRecent)));
				
				// value of difference needs to be considered relative to current disposal
				AssetBalance balance = new AssetBalance(
						newBalanceAtMostRecent.getAssetName(), 
						newBalanceAtMostRecent.getAssetAmount(),
						newBalanceAtMostRecent.getExchangeRateAUD(),
						disposal.getBalanceDate(),
						BalanceType.Sum);
				
				// return the stream. ohhh dear // TODO recursive, can it be flattened...
				stream = Stream.concat(stream, createCGTEventsForDisposal(balanceList).apply(balance));
				
				
			} else {
				
				// update most recent with new balance
				int idx = balanceList.indexOf(mostRecent);
				balanceList.remove(mostRecent);
				balanceList.add(idx, newBalanceAtMostRecent);
				
				stream = Stream.concat(stream, 
						Stream.of(createCGTEvent().apply(disposal, mostRecent)));
				
			}	

			return stream;
			
		};
		
	}

	public List<CGTEvent> calculateCGTByAsset(Map<String, List<AssetBalance>> map){
		
		return map.values()
			.stream()
			.flatMap(assetBalances->{
				
				// a - value corresponds to a disposal
				List<AssetBalance> disposals = assetBalances.stream().filter(cob->cob.getAssetAmount()<0.0)
									.collect(Collectors.toList());
				
				// a + value corresponds to a buy / purchase
				List<AssetBalance> purchases = assetBalances.stream().filter(cob->cob.getAssetAmount()>0.0)
						.collect(Collectors.toList()); 
				
				// TODO this function is stateful and updates the input list
				List<AssetBalance> purchasesCpy = new ArrayList<AssetBalance>(purchases);
				Function<AssetBalance, Stream<CGTEvent>> state = createCGTEventsForDisposal(purchasesCpy);
				List<CGTEvent> cgts = getCGTSForDisposals(state).apply(disposals);
				return cgts.stream();
				
			})
			.collect(Collectors.toList());
			
	}
	
}
