package com.hdekker.cryptocgt.imports.coinspot;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.data.AssetBalance.BalanceType;
import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.data.transaction.TransactionType;

@Component
public class CoinspotOrderAssetBalanceConverter {
	
	public static List<String> splitMarketString(String marketString){
		return  Arrays.asList(marketString.split("/"));
	}
	
	public static Function<String, Double> splitTargetAmount(){
		return (s)-> Double.valueOf(s.split(" ")[0]);
	}
	
	public static Function<Order, Double> calcPrimaryExchangeRateAUD(){
		return (ord) -> ord.getTotalAUD()/ord.getAmount();
	}
	
	public static Function<Order, Double> calcSecondaryExchangeRateAUD(){
		return (ord) -> ord.getTotalAUD()/getSecondayAmount().apply(ord);
		
	}
	
	public static Function<Order, Double> getSecondayAmount(){
		return (order) -> splitTargetAmount().apply(order.getTotalIncGST());
	}
	
	public static Function<Order, Double> calcAmountPrimary(){
		return (order) -> order.getTransactionType().equals(TransactionType.Sell) 
												? order.getAmount() *-1 : order.getAmount();
	}
	
	public static Function<Order, Double> calcAmountSecondary(){
		return (order) -> order.getTransactionType().equals(TransactionType.Sell) 
						? splitTargetAmount().apply(order.getTotalIncGST()) : 
							splitTargetAmount().apply(order.getTotalIncGST()) *-1.0;
	}

	private Stream<AssetBalance> convert(Order order){
		
			List<String> market = splitMarketString(order.getMarket());
			
			AssetBalance cob1 = new AssetBalance(
					market.get(0),
					calcAmountPrimary().apply(order),
					calcPrimaryExchangeRateAUD().apply(order),
					order.getTransactionDate(),
					BalanceType.Transaction
					);
			
			AssetBalance cob2 = new AssetBalance(
					market.get(1),
					calcAmountSecondary().apply(order),
					calcSecondaryExchangeRateAUD().apply(order),
					order.getTransactionDate(),
					BalanceType.Transaction
					);
		
			return Stream.of(cob1, cob2);
			
	}
	
	public List<AssetBalance> getAssetBalancesForOrders(List<Order> list){

		List<AssetBalance> balances = list
				.stream()
				.flatMap(order-> convert(order))
				.collect(Collectors.toList());

		return balances;

	}
	
}
