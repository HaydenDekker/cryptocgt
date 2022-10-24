package com.hdekker.cryptocgt.cgt;

import static com.hdekker.cryptocgt.balance.BalanceAssesment.sumCoinOrderBalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.cgt.search.MoreThan12MonthDateSearcher;
import com.hdekker.cryptocgt.cgt.search.MostRecentDateSearcher;
import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.data.CGTEvent;
import com.hdekker.cryptocgt.data.AssetBalance.BalanceType;

@Component
public class CGTCalculator {
	
	Logger log = LoggerFactory.getLogger(CGTCalculator.class);

	
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
	
	public Stream<CGTEvent> getCGTEvents(AssetBalance disposal, List<AssetBalance> purchases){
		
		Stream<CGTEvent> stream = Stream.empty();
		
		AssetBalance purchaseToClaimSoldFrom = null;
		
		// TODO section is horrible.
		
		if(disposal.getAssetAmount()>0) {
			purchaseToClaimSoldFrom = moreThan12MonthSearcher.search(AssetBalance::getBalanceDate)
					.apply(disposal, purchases)
					.orElse(null);
		}
		
		try {
		// TODO this needs to change to a maximise return function.
	    // Choose item over 12 months if available else choose most recent.
		// That way more purchases have a potential to reach the 12 month discount
		// marker.
		
		if(purchaseToClaimSoldFrom==null) {
			purchaseToClaimSoldFrom = mostRecentDateSearcher.search(AssetBalance::getBalanceDate)
					.apply(disposal, purchases)
					.orElseThrow();
		}
		}
		catch(Exception e) {

			if(disposal.getAssetAmount()*disposal.getExchangeRateAUD()>1) {
				log.error("This was too big for just a rounding issue");
			}
			
			return Stream.empty();
		}
		AssetBalance newBalanceAtMostRecent = sumCoinOrderBalance().apply(purchaseToClaimSoldFrom, disposal);
			
		// add to stream if new balance positive and get next
		if(newBalanceAtMostRecent.getAssetAmount()<0) {
				
			// remove most recent creating new list
			purchases.remove(purchaseToClaimSoldFrom);
			
			// create cgt for removal
			stream = Stream.concat(stream, Stream.of(createCGTEvent().apply(disposal, purchaseToClaimSoldFrom)));
			
			// value of difference needs to be considered relative to current disposal
			AssetBalance balance = new AssetBalance(
					newBalanceAtMostRecent.getAssetName(), 
					newBalanceAtMostRecent.getAssetAmount(),
					newBalanceAtMostRecent.getExchangeRateAUD(),
					disposal.getBalanceDate(),
					BalanceType.Sum);
			
			// return the stream. ohhh dear // TODO recursive, can it be flattened...
			stream = Stream.concat(stream, getCGTEvents(balance, purchases));
			
			
		} else {
			
			// update most recent with new balance
			int idx = purchases.indexOf(purchaseToClaimSoldFrom);
			purchases.remove(purchaseToClaimSoldFrom);
			purchases.add(idx, newBalanceAtMostRecent);
			
			stream = Stream.concat(stream, 
					Stream.of(createCGTEvent().apply(disposal, purchaseToClaimSoldFrom)));
			
		}	

		return stream;
		
	}
	
	public List<CGTEvent> createCGTEventsForDisposal(List<AssetBalance> purchases, List<AssetBalance> disposals){
		
		// statefull list for as long as the function is used.
		return disposals
			.stream()
			.flatMap(d->{
				return getCGTEvents(d, purchases);
			})
			.collect(Collectors.toList());
		
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
				List<CGTEvent> cgts = createCGTEventsForDisposal(purchasesCpy, disposals);
				 
				return cgts.stream();
				
			})
			.collect(Collectors.toList());
			
	}
	
}
