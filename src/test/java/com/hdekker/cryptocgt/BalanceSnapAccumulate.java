package com.hdekker.cryptocgt;

import org.assertj.core.util.Streams;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.data.AccountOrderSnapshot;
import com.hdekker.cryptocgt.data.CoinOrderBalance;
import com.hdekker.cryptocgt.data.Order;
import com.hdekker.cryptocgt.interfaces.CSVUtils.Converters;

import static com.hdekker.cryptocgt.interfaces.BalanceSnapshotUtils.*;
import static com.hdekker.cryptocgt.interfaces.AccountOrderUtils.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class BalanceSnapAccumulate {

	Logger logg = LoggerFactory.getLogger(BalanceSnapAccumulate.class);
	
	//3/1/2019 12:35	Sell	BTC/ETH	0.01	24.77630004	25.0265657	0.00250266 ETH	0.55	0.05	54.66	0.24776300 ETH

	Order testOrder2() {
		
		Order o = new Order();
		o.setTransactionDate(Converters.dateTimeConverter.apply("3/1/2019 12:35"));
		o.setTransactionType(TransactionType.Sell);
		o.setMarket("BTC/ETH");
		o.setAmount(Converters.doubleConverter.apply("0.01"));
		o.setTotalAUD(Converters.doubleConverter.apply("54.66"));
		o.setTotalIncGST("0.24776300 ETH");
		
		return o;
		
	}
	
	// 13/6/2017 17:22	Sell	BTC/ETH	0.014	7.07041801	7.07041801	0.00000000 ETH	0	0	52.37	0.09898585 ETH
	Order testOrder3() {
		
		Order o = new Order();
		o.setTransactionDate(Converters.dateTimeConverter.apply("13/6/2017 17:22"));
		o.setTransactionType(TransactionType.Sell);
		o.setMarket("BTC/ETH");
		o.setAmount(Converters.doubleConverter.apply("0.014"));
		o.setTotalAUD(Converters.doubleConverter.apply("52.37"));
		o.setTotalIncGST("0.09898585 ETH");
		
		return o;
		
	}
	
	Order testOrder1() {
		
		Order o = new Order();
		o.setTransactionDate(Converters.dateTimeConverter.apply("21/5/2017 16:51"));
		o.setTransactionType(TransactionType.Sell);
		o.setMarket("BTC/ETH");
		o.setAmount(Converters.doubleConverter.apply("0.01"));
		o.setTotalAUD(Converters.doubleConverter.apply("28.31"));
		o.setTotalIncGST("0.16363634 ETH");
		
		return o;
		
	}

	@Test
	public void balance() {
		
		AccountOrderSnapshot os1 = createOrderSnapshot().apply(testOrder1());
		AccountOrderSnapshot os2 = createOrderSnapshot().apply(testOrder2());
		AccountOrderSnapshot os3 = createOrderSnapshot().apply(testOrder3());
		
		List<AccountOrderSnapshot> snaps = Arrays.asList(os1, os2, os3);
		List<CoinOrderBalance> coins = snaps.stream().flatMap(snap-> streamBalances().apply(snap))
							.collect(Collectors.toList());
		
		Optional<Double> btcTrans = reduceBalanceForCoin(coins).apply("BTC");
		
		btcTrans.ifPresent(acc->logg.info("The test for balance calculations of BTC came to " + acc));
		 
		assertTrue(btcTrans.get().equals(-0.034));
		
	}
	
}
