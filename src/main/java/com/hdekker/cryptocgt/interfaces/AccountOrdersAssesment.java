package com.hdekker.cryptocgt.interfaces;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.hdekker.cryptocgt.data.AccountOrderSnapshot;
import com.hdekker.cryptocgt.data.CoinOrderBalance;
import com.hdekker.cryptocgt.data.TransactionType;
import com.hdekker.cryptocgt.data.transaction.Order;

/**
 * Interface to convert Order's to Account Order Snapshots
 * An intermediary type used in calc of CGT
 * 
 * @author HDekker
 *
 */
public interface AccountOrdersAssesment {

		// Order Maarshling functions 
		// This set of functions converts the order's values
		// into value required by the AccountOrderSnapshot
		public static BiFunction<String, Boolean, String> getPrimaryOrSecondaryCoinName(){
			return (market, returnPrimary) -> (returnPrimary) ? market.split("/")[0] : market.split("/")[1];
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
				AccountOrderSnapshot aos = new AccountOrderSnapshot();
				aos.setSnapshotDate(order.getTransactionDate());
				aos.setType(order.getTransactionType());
				
				// check for a buy condition
				//Boolean isBuyOrder = order.getTransactionType().equals(TransactionType.Buy);
				
				CoinOrderBalance cob1 = new CoinOrderBalance();
				cob1.setCoinName(getPrimaryOrSecondaryCoinName().apply(order.getMarket(), true));
				Double prAmt = calcAmountPrimary().apply(order);
				cob1.setCoinAmount(prAmt);//isBuyOrder ? -1*prAmt : prAmt);
				cob1.setExchangeRateAUD(calcPrimaryExchangeRateAUD().apply(order));
				cob1.setBalanceDate(order.getTransactionDate());
				aos.setCob1(cob1);
				
				CoinOrderBalance cob2 = new CoinOrderBalance();
				cob2.setCoinName(getPrimaryOrSecondaryCoinName().apply(order.getMarket(), false));
				Double secAmt = calcAmountSecondary().apply(order);
				cob2.setCoinAmount(secAmt);//isBuyOrder ? -1*secAmt : secAmt);
				cob2.setExchangeRateAUD(calcSecondaryExchangeRateAUD().apply(order));
				cob2.setBalanceDate(order.getTransactionDate());
				aos.setCob2(cob2);
				
				return aos;
			};
		}
		
	
}
