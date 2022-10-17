package com.hdekker.cryptocgt;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.data.AccountOrderSnapshot;
import com.hdekker.cryptocgt.data.CoinBalance;
import com.hdekker.cryptocgt.data.CoinOrderBalance;
import com.hdekker.cryptocgt.data.TransactionType;
import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.data.transaction.SendRecieves;
import com.hdekker.cryptocgt.interfaces.AccountOrdersAssesment;
import com.hdekker.cryptocgt.interfaces.BalanceAssesment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class BalanceAssesmentTest {

	
	Double btcd1 = 2.30;
	Double btcd2 = 2.45;
	Double btcd4 = 2.11;
	
	// 21/5/2017 16:51	Sell	BTC/ETH	0.01	16.36363366	16.36363366	0.00000000 ETH	0	0	28.31	0.16363634 ETH

	Order testOrder1() {
		
		Order o = new Order(
				LocalDateTime.of(2017, 5, 21, 16, 51),
				TransactionType.Sell,
				"BTC/ETH",
				0.01,
				28.31,
				"0.16363634 ETH");
		
		return o;
		
	}
	
	//3/1/2019 12:35	Sell	BTC/ETH	0.01	24.77630004	25.0265657	0.00250266 ETH	0.55	0.05	54.66	0.24776300 ETH
	
	Order testOrder2() {
		
		Order o = new Order(LocalDateTime.of(2019, 1, 3, 12, 35),
				TransactionType.Sell,
				"BTC/ETH",
				0.01,
				54.66,
				"0.24776300 ETH"
				);
		
		return o;
		
	}

	// 10/12/2017 20:27	Buy	BTC/AUD	0.0046118	20599.34	20195.43133	1.86274580 AUD	1.86	0.17	95	95.00 AUD

	
//	Order testOrder4() {
//		
//		Order o = new Order();
//		o.setTransactionDate(Converters.dateTimeConverter.apply("10/12/2017 20:27"));
//		o.setTransactionType(TransactionType.Buy);
//		o.setMarket("BTC/AUD");
//		o.setAmount(Converters.doubleConverter.apply("0.0046118"));
//		o.setTotalAUD(Converters.doubleConverter.apply("95"));
//		o.setTotalIncGST("95.00 AUD");
//		
//		return o;
//		
//	}

//	AccountBalanceSnapshot newSnapshot(){
//		return new AccountBalanceSnapshot();
//	}
//	
	
	// For summing each order bit by bit.X
	// program must ensure coin types are the same
	
	// 13/6/2017 17:22	Sell	BTC/ETH	0.014	7.07041801	7.07041801	0.00000000 ETH	0	0	52.37	0.09898585 ETH
	Order testOrder3() {
		
		Order o = new Order(
				LocalDateTime.of(2017, 6, 13, 17, 22),
				TransactionType.Sell,
				"BTC/ETH",
				0.014,
				52.37,
				"0.09898585 ETH"
				);
		
		return o;
		
	}

	@Test
	public void addBalance() {
		
		CoinBalance cb1 = new CoinBalance();
		cb1.setCoinAmount(0.234);
		CoinBalance cb2 = new CoinBalance();
		cb2.setCoinAmount(0.266);
		
		BiFunction<CoinBalance, CoinBalance, CoinBalance> fn = BalanceAssesment.sumCoinBalance();
		
		CoinBalance cb = fn.apply(cb1, cb2);
		assertThat(cb.getCoinAmount(), equalTo(0.5));
		
	}
	
	Logger logg = LoggerFactory.getLogger(BalanceAssesmentTest.class);
	
	@Test
		public void uReducesBalanceForAnAsset() {
			
		Function<Order, AccountOrderSnapshot> fn = AccountOrdersAssesment.createOrderSnapshot();
		
			AccountOrderSnapshot os1 = fn.apply(testOrder1());
			AccountOrderSnapshot os2 = fn.apply(testOrder2());
			AccountOrderSnapshot os3 = fn.apply(testOrder3());
			
			List<AccountOrderSnapshot> snaps = Arrays.asList(os1, os2, os3);
			
			Function<AccountOrderSnapshot, Stream<CoinOrderBalance>> balanceFn = BalanceAssesment.streamBalances();
			
			List<CoinOrderBalance> coins = snaps.stream()
								.flatMap(snap-> balanceFn.apply(snap))
								.collect(Collectors.toList());
			
			Function<String, Optional<Double>> rFn = BalanceAssesment.reduceBalanceForCoin().apply(coins);
			
			Optional<Double> btcTrans = rFn
												.apply("BTC");
			
			btcTrans.ifPresent(acc->logg.info("The test for balance calculations of BTC came to " + acc));
			 
			assertThat(btcTrans.get(), equalTo(-0.034));
			
		}
	

	@Test
	public void uCalcCOBsForSendRecieves() {
		
		Function<SendRecieves, CoinOrderBalance> fn = BalanceAssesment.sendRecieveToCoinOrderBalance();
		
		// TODO implements
//		List<CoinOrderBalance> srCobs = srConfig.getSendRecieves(userConfig.getSendsReceivesCSV())
//						.stream()
//						.map(sr-> sendRecieveToCoinOrderBalance().apply(sr))
//						.collect(Collectors.toList());
//		
		//assertTrue(srCobs.size()==12);
		
	}
	
	
}
