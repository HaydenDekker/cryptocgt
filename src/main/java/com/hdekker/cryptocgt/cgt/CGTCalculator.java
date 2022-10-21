package com.hdekker.cryptocgt.cgt;

import static com.hdekker.cryptocgt.balance.BalanceAssesment.sumCoinOrderBalance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
	
	public static <T> BiFunction<T, List<T>, T> findTheMostRecentPurchase(Function<T, LocalDateTime> dtProvider){
		return (cob, list) -> {
			
			// reverse order. I'm sure this would be horrible for long lists.
			List<T> sorted = list.stream().sorted((cob1, cob2)-> dtProvider.apply(cob2).compareTo(dtProvider.apply(cob1)))
														.collect(Collectors.toList());
			return sorted.stream()
					.filter(cob1->dtProvider.apply(cob).compareTo(dtProvider.apply(cob1))>0)
					.findFirst().orElseThrow();
		};
	}
	
	public static Function<AssetBalance, Stream<CGTEvent>> createCGTEventsForDisposal(List<AssetBalance> balanceList){
		
		// statefull list for as long as the function is used.
		Logger log = LoggerFactory.getLogger(CGTUtils.class);
		
		return (disposal) -> {
			
			Stream<CGTEvent> stream = Stream.empty();
			
			AssetBalance mostRecent = null;
			
			try {
			mostRecent = findTheMostRecentPurchase(AssetBalance::getBalanceDate)
					.apply(disposal, balanceList);
			//log.info("Most recent purchase is on " + mostRecent.getBalanceDate() + " of " + mostRecent.getCoinAmount());
			
			}
			catch(Exception e) {
				
				log.info("Couldn't find most recent purchase to match the sale.");
				log.info(balanceList.size() + " is the size of the balance list.");
				log.info("the disposal was " + Utils.toJson(AssetBalance.class).apply(disposal));
				
				if(disposal.getAssetAmount()*disposal.getExchangeRateAUD()>1) {
					log.info("This was too big for just a rounding issue");
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
				
				// return the stream.
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
