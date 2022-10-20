package com.hdekker.cryptocgt.balance;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.data.AssetBalance.BalanceType;

public class BalanceAssesment {
	
	public static BiFunction<AssetBalance, AssetBalance, AssetBalance> sumCoinOrderBalance(){
		return (cb1Earliest, cb2) -> {
			
			double sum = cb2.getAssetAmount() + cb1Earliest.getAssetAmount();
			
			AssetBalance cb = new AssetBalance(
					cb1Earliest.getAssetName(), 
					sum, 
					cb1Earliest.getExchangeRateAUD(),
					cb1Earliest.getBalanceDate(),
					BalanceType.Sum
					);
			
			return cb;
			
		};
	}
	
	// TODO filter to a map of cointype by balances first then
	// reduce each
	public static 
		Function<List<AssetBalance>, 
				Function<String, Optional<Double>>> reduceBalanceForCoin(){
		
		return (allBalances) -> (coinName) -> {
			return allBalances.stream().filter(s->s.getAssetName().equals(coinName))
				.map(c->c.getAssetAmount())
			.reduce((a,b)-> a+b);
		};
		
	}
	
}
