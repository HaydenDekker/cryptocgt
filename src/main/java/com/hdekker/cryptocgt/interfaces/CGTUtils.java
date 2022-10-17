package com.hdekker.cryptocgt.interfaces;

import static com.hdekker.cryptocgt.interfaces.BalanceAssesment.sendRecieveToCoinOrderBalance;
import static com.hdekker.cryptocgt.interfaces.BalanceAssesment.streamBalances;
import static com.hdekker.cryptocgt.interfaces.BalanceAssesment.sumCoinOrderBalance;
import static com.hdekker.cryptocgt.interfaces.CGTUtils.convertToHashMapAndSortByKeyValue;
import static com.hdekker.cryptocgt.interfaces.CGTUtils.getCoinBalancesForOrders;
import static com.hdekker.cryptocgt.interfaces.AccountOrdersAssesment.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hdekker.cryptocgt.data.AccountOrderSnapshot;
import com.hdekker.cryptocgt.data.CGTEvent;
import com.hdekker.cryptocgt.data.CoinOrderBalance;
import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.data.transaction.SendRecieves;

public interface CGTUtils {

	public static 
	Function<Function<CoinOrderBalance, String>,
		Function<List<CoinOrderBalance>, Map<String, List<CoinOrderBalance>>>> convertToHashMapAndSortByKeyValue(){
		return (keyMapper) ->  (list) -> {
			
			// just want to accumulate like cob's
			BinaryOperator<List<CoinOrderBalance>> merger = (a, b) -> {
				
				List<CoinOrderBalance> nl = new ArrayList<>(a);
				nl.addAll(b);
				return nl;
				
			};
			
			return list.stream()
				.collect(Collectors.toMap(keyMapper, cob -> Arrays.asList(cob), merger));
			
//			HashMap<String, List<CoinOrderBalance>> balanceMap = new HashMap<>();
//			
//			for(CoinOrderBalance cob: list){
//				
//				String key = keySupplier.apply(cob);
//				balanceMap.put(key, addBalanceToList()
//											.apply(Optional.ofNullable(balanceMap.get(key))
//														.orElse(new ArrayList<>())
//														,cob
//											)
//								);
//			}
			
		};
	}
	
	public static BiFunction<List<CoinOrderBalance>, CoinOrderBalance, List<CoinOrderBalance>> addBalanceToList(){
		return (list, bal)-> {
			List<CoinOrderBalance> l = new ArrayList<>(list);
			l.add(bal);
			return l;
		};
	}
	
	public static Function<List<Order>, List<CoinOrderBalance>> getCoinBalancesForOrders(){
		
		return (list) ->{
		List<AccountOrderSnapshot> snaps = list.stream().map(order->createOrderSnapshot().apply(order))
				.collect(Collectors.toList());


		List<CoinOrderBalance> balances = snaps.stream()
		.flatMap(snap-> streamBalances().apply(snap))
		.collect(Collectors.toList());

		return balances;
		};
	}
	
	public static Function<String, List<CGTEvent>> getCGTUsingOrdersSendsAndRecieves(Map<String, List<CoinOrderBalance>> map){
		
		return (coin) ->{
			
			
			// test BTC's
			List<CoinOrderBalance> btcs = map.get(coin);
			
			// a - value corresponds to a disposal
			List<CoinOrderBalance> disposals = btcs.stream().filter(cob->cob.getCoinAmount()<0.0)
								.collect(Collectors.toList());
			
			// a + value corresponds to a buy / purchase
			List<CoinOrderBalance> purchases = btcs.stream().filter(cob->cob.getCoinAmount()>0.0)
					.collect(Collectors.toList());
			
//			try {
//				log.info(om.writeValueAsString(disposals));
//				log.info(om.writeValueAsString(purchases));
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	
			
			// need to find preceeding cob where there is balance left to create a CGT snapshot
			//btcs.
			// TODO this function is stateful and updates the input list
			List<CoinOrderBalance> purchasesCpy = new ArrayList<CoinOrderBalance>(purchases);
			Function<CoinOrderBalance, Stream<CGTEvent>> state = createCGTEventsForDisposal(purchasesCpy);
			List<CGTEvent> cgts = getCGTSForDisposals(state).apply(disposals);
			
			return cgts;
			
		};
		
	}
	
	public static BiFunction<CoinOrderBalance, CoinOrderBalance, CGTEvent> createCGTEvent(){
		
		return (cobCurrent, cobPast)-> {
			
			CGTEvent e = new CGTEvent();
			e.setCoinName(cobCurrent.getCoinName());
			e.setDisposedDate(cobCurrent.getBalanceDate());
			e.setPurchasedDate(cobPast.getBalanceDate());
			
			Double purchaseCost = Math.abs(cobCurrent.getCoinAmount())*cobPast.getExchangeRateAUD();
			Double sellPrice = Math.abs(cobCurrent.getCoinAmount())*cobCurrent.getExchangeRateAUD();
			
			e.setCgt(sellPrice-purchaseCost);
			
			return e;
			
		};
		
	}
	
	public static Function<CoinOrderBalance, Stream<CGTEvent>> createCGTEventsForDisposal(List<CoinOrderBalance> balanceList){
		
		// statefull list for as long as the function is used.
		Logger log = LoggerFactory.getLogger(CGTUtils.class);
		
		return (disposal) -> {
			
			Stream<CGTEvent> stream = Stream.empty();
			
			CoinOrderBalance mostRecent = null;
			
			try {
			mostRecent = findTheMostRecentPurchase().apply(disposal, balanceList);
			//log.info("Most recent purchase is on " + mostRecent.getBalanceDate() + " of " + mostRecent.getCoinAmount());
			
			}
			catch(Exception e) {
				
				log.info("Couldn't find most recent purchase to match the sale.");
				log.info(balanceList.size() + " is the size of the balance list.");
				log.info("the disposal was " + Utils.toJson(CoinOrderBalance.class).apply(disposal));
				
				if(disposal.getCoinAmount()*disposal.getExchangeRateAUD()>1) {
					log.info("This was too big for just a rounding issue");
				}
				
				return Stream.empty();
			}
			CoinOrderBalance newBalanceAtMostRecent = sumCoinOrderBalance().apply(mostRecent, disposal);
				
			// add to stream if new balance positive and get next
			if(newBalanceAtMostRecent.getCoinAmount()<0) {
					
				// remove most recent creating new list
				balanceList.remove(mostRecent);
				
				// create cgt for removal
				stream = Stream.concat(stream, Stream.of(createCGTEvent().apply(disposal, mostRecent)));
				
				// value of difference needs to be considered relative to current disposal
				newBalanceAtMostRecent.setBalanceDate(disposal.getBalanceDate());
				
				// return the stream.
				
				stream = Stream.concat(stream, createCGTEventsForDisposal(balanceList).apply(newBalanceAtMostRecent));
				
				
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
	
	public static Function<List<CoinOrderBalance>, List<CGTEvent>> getCGTSForDisposals(Function<CoinOrderBalance, Stream<CGTEvent>> cgtGetter){
		return (disposals) ->{
			
			return disposals.stream()
						.flatMap(d-> {
							
							//log.info("Starting disposal of " + d.getCoinName() + " on " + d.getBalanceDate().toString() + " and amount " + d.getCoinAmount());
							return cgtGetter.apply(d);
						})
						.collect(Collectors.toList());
			
		};
	}
	
	public static BiFunction<List<Order>, List<SendRecieves>, Map<String, List<CoinOrderBalance>>> combineAndMapOrdersAndSendRecievesToCobs(){
			
			return (ord, srs) -> {
				
				List<CoinOrderBalance> orders = getCoinBalancesForOrders().apply(ord);
				List<CoinOrderBalance> sendReceives = srs
														.stream().map(sr-> sendRecieveToCoinOrderBalance().apply(sr))
														.collect(Collectors.toList());
				List<CoinOrderBalance> comb = new ArrayList<>(orders);
				comb.addAll(sendReceives);
				Map<String, List<CoinOrderBalance>> map = convertToHashMapAndSortByKeyValue().apply(CoinOrderBalance::getCoinName).apply(comb);
				map.keySet().forEach(key->map.get(key).sort((cob1, cob2) -> cob1.getBalanceDate().compareTo(cob2.getBalanceDate())));
				return map;
				
			};
			
		}
		
	

	public static BiFunction<CoinOrderBalance, List<CoinOrderBalance>, CoinOrderBalance> findTheMostRecentPurchase(){
		return (cob, list) -> {
			
			// reverse order. I'm sure this would be horrible for long lists.
			List<CoinOrderBalance> sorted = list.stream().sorted((cob1, cob2)-> cob2.getBalanceDate().compareTo(cob1.getBalanceDate()))
														.collect(Collectors.toList());
			return sorted.stream()
					.filter(cob1->cob.getBalanceDate().compareTo(cob1.getBalanceDate())>0)
					.findFirst().orElseThrow();
		};
	}
	
	public static Function<List<CGTEvent>, Double> cgtSummer(){
		return (list) -> list.stream()
								.map(e->e.getCgt())
								.reduce((a,n)-> a+n).orElse(0.0);
	}
	
	public static Function<List<CGTEvent>, List<CGTEvent>> dateFilter(LocalDateTime after, LocalDateTime before){
	
		return (list) -> list.stream().filter(l->{
			
			return l.getDisposedDate().compareTo(after)>0 && l.getDisposedDate().compareTo(before)<0;
		}).collect(Collectors.toList());
			
	}
	
}
