package com.hdekker.cryptocgt.orders;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.hdekker.cryptocgt.data.AccountOrderSnapshot;
import com.hdekker.cryptocgt.data.CoinOrderBalance;
import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.data.transaction.TransactionType;

/**
 * An intermediary type used in calc of CGT
 * 
 * @author HDekker
 *
 */
public class AccountOrdersAssesment {

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
		
		public static Function<Order, AccountOrderSnapshot> createOrderSnapshot(){
			
			return (order) ->{
				
				List<String> market = splitMarketString(order.getMarket());
				// todo clean up functions
				CoinOrderBalance cob1 = new CoinOrderBalance(
						market.get(0),
						calcAmountPrimary().apply(order),
						calcPrimaryExchangeRateAUD().apply(order),
						order.getTransactionDate()
						);
				
				CoinOrderBalance cob2 = new CoinOrderBalance(
						market.get(1),
						calcAmountSecondary().apply(order),
						calcSecondaryExchangeRateAUD().apply(order),
						order.getTransactionDate()
						
						);
				
				AccountOrderSnapshot aos = new AccountOrderSnapshot(
						order.getTransactionDate(),
						cob1,
						cob2,
						order.getTransactionType()
						);
		
				return aos;
			};
		}
		
	
}
