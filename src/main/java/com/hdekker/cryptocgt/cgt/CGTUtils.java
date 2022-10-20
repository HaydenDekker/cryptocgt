package com.hdekker.cryptocgt.cgt;

import static com.hdekker.cryptocgt.balance.BalanceAssesment.sumCoinOrderBalance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.data.CGTEvent;
import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.data.AssetBalance.BalanceType;
import com.hdekker.cryptocgt.reports.Utils;

@Component
public class CGTUtils {

	public static 
	Function<Function<AssetBalance, String>,
		Function<List<AssetBalance>, Map<String, List<AssetBalance>>>> convertToHashMap(){
		return (keyMapper) ->  (list) -> {
			
			// just want to accumulate like cob's
			BinaryOperator<List<AssetBalance>> merger = (a, b) -> {
				
				List<AssetBalance> nl = new ArrayList<>(a);
				nl.addAll(b);
				return nl;
				
			};
			
			return list.stream()
				.collect(Collectors.toMap(keyMapper, cob -> Arrays.asList(cob), merger));
			
		};
	}
	
	public static BiFunction<List<AssetBalance>, AssetBalance, List<AssetBalance>> addBalanceToList(){
		return (list, bal)-> {
			List<AssetBalance> l = new ArrayList<>(list);
			l.add(bal);
			return l;
		};
	}
	
	public static Function<String, List<CGTEvent>> getCGTUsingOrdersSendsAndRecieves(Map<String, List<AssetBalance>> map){
		
		return (coin) ->{
			
			
			// test BTC's
			List<AssetBalance> btcs = map.get(coin);
			
			// a - value corresponds to a disposal
			List<AssetBalance> disposals = btcs.stream().filter(cob->cob.getAssetAmount()<0.0)
								.collect(Collectors.toList());
			
			// a + value corresponds to a buy / purchase
			List<AssetBalance> purchases = btcs.stream().filter(cob->cob.getAssetAmount()>0.0)
					.collect(Collectors.toList()); 
			
			// TODO this function is stateful and updates the input list
			List<AssetBalance> purchasesCpy = new ArrayList<AssetBalance>(purchases);
			Function<AssetBalance, Stream<CGTEvent>> state = createCGTEventsForDisposal(purchasesCpy);
			List<CGTEvent> cgts = getCGTSForDisposals(state).apply(disposals);
			
			return cgts;
			
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
	
	public Map<String, List<AssetBalance>> mapAssetBalancesByAssetName(List<AssetBalance> balances){
				
				Map<String, List<AssetBalance>> map = convertToHashMap()
						.apply(AssetBalance::getAssetName)
						.apply(balances);
				
				map.keySet()
					.forEach(
						key->map.get(key)
								.sort((cob1, cob2) -> 
									cob1.getBalanceDate()
									.compareTo(cob2.getBalanceDate())
								)
								
							);
				
				return map;

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
	
	// TODO only used in reporting of final CGT per year.
	public static Function<List<CGTEvent>, Double> cgtSummer(){
		return (list) -> list.stream()
								.map(e->e.getCgt())
								.reduce((a,n)-> a+n).orElse(0.0);
	}
	
	// TODO only used in reporting of final CGT per year.
	public static Function<List<CGTEvent>, List<CGTEvent>> dateFilter(LocalDateTime after, LocalDateTime before){
	
		return (list) -> list.stream().filter(l->{
			
			return l.getDisposedDate().compareTo(after)>0 && l.getDisposedDate().compareTo(before)<0;
		}).collect(Collectors.toList());
			
	}
	
}
