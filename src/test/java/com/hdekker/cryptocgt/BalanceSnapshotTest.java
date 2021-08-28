package com.hdekker.cryptocgt;

import static com.hdekker.cryptocgt.interfaces.BalanceSnapshotUtils.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.data.AccountBalanceSnapshot;
import com.hdekker.cryptocgt.data.CoinBalance;
import com.hdekker.cryptocgt.data.Order;
import com.hdekker.cryptocgt.interfaces.CSVUtils.Converters;

@SpringBootTest
public class BalanceSnapshotTest {

	
	Double btcd1 = 2.30;
	Double btcd2 = 2.45;
	Double btcd4 = 2.11;
	
	// 21/5/2017 16:51	Sell	BTC/ETH	0.01	16.36363366	16.36363366	0.00000000 ETH	0	0	28.31	0.16363634 ETH

	
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
	
	// 10/12/2017 20:27	Buy	BTC/AUD	0.0046118	20599.34	20195.43133	1.86274580 AUD	1.86	0.17	95	95.00 AUD

	
	Order testOrder2() {
		
		Order o = new Order();
		o.setTransactionDate(Converters.dateTimeConverter.apply("10/12/2017 20:27"));
		o.setTransactionType(TransactionType.Buy);
		o.setMarket("BTC/AUD");
		o.setAmount(Converters.doubleConverter.apply("0.0046118"));
		o.setTotalAUD(Converters.doubleConverter.apply("95"));
		o.setTotalIncGST("95.00 AUD");
		
		return o;
		
	}

//	
//	Function<Order, AccountBalanceSnapshot> captureNewBalance(){
//		return (snap, order) -> {
//			
//		
//			
//			return null;
//		};
//	}
	
	AccountBalanceSnapshot newSnapshot(){
		return new AccountBalanceSnapshot();
	}
	
	
	// For summing each order bit by bit.X
	// program must ensure coin types are the same
	
	@Test
	public void addBalance() {
		
		CoinBalance cb1 = new CoinBalance();
		cb1.setCoinAmount(0.234);
		CoinBalance cb2 = new CoinBalance();
		cb2.setCoinAmount(0.266);
		
		CoinBalance cb = sumCoinBalance().apply(cb1, cb2);
		assertTrue(cb.getCoinAmount().equals(0.5));
		
	}
	
	
}
