package com.hdekker.cryptocgt.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import com.hdekker.cryptocgt.TransactionType;
import com.hdekker.cryptocgt.data.AccountOrderSnapshot;
import com.hdekker.cryptocgt.data.CoinBalance;
import com.hdekker.cryptocgt.data.CoinOrderBalance;
import com.hdekker.cryptocgt.data.Order;
import com.hdekker.cryptocgt.imports.SendRecieves;
import com.hdekker.cryptocgt.imports.CSVUtils.Converters;

public interface BalanceAssesment {

	public static BiFunction<CoinBalance, CoinBalance, CoinBalance> sumCoinBalance(){
		return (cb1, cb2) -> {
			
			CoinBalance cb = Utils.coinBalanceDeepCopy().apply(cb2);
			cb.setCoinAmount(cb2.getCoinAmount() + cb1.getCoinAmount());
			return cb;
			
		};
	}
	
	public static BiFunction<CoinOrderBalance, CoinOrderBalance, CoinOrderBalance> sumCoinOrderBalance(){
		return (cb1Earliest, cb2) -> {
			
			CoinOrderBalance cb = Utils.deepCopier(CoinOrderBalance.class).apply(cb1Earliest);
			cb.setCoinAmount(cb2.getCoinAmount() + cb1Earliest.getCoinAmount());
			return cb;
			
		};
	}
	
	// TODO filter to a map of cointype by balances first then
	// reduce each
	public static 
		Function<List<CoinOrderBalance>, 
				Function<String, Optional<Double>>> reduceBalanceForCoin(){
		
		return (allBalances) -> (coinName) -> {
			return allBalances.stream().filter(s->s.getCoinName().equals(coinName))
				.map(c->c.getCoinAmount())
			.reduce((a,b)-> a+b);
		};
		
	}
	
	public static Function<AccountOrderSnapshot, Stream<CoinOrderBalance>> streamBalances(){
		return (snap) -> Stream.of(snap.getCob1(), snap.getCob2());
	}
	
	public static Function<SendRecieves, CoinOrderBalance> sendRecieveToCoinOrderBalance(){
		return (sr) -> {
			
			CoinOrderBalance cob = new CoinOrderBalance();
			cob.setCoinAmount(sr.getAmount());
			cob.setCoinName(sr.getCoin());
			cob.setExchangeRateAUD(sr.getExchangeRateAUD());
			cob.setBalanceDate(sr.getTransactionDate());
			
			return cob;
		};
	}
}
