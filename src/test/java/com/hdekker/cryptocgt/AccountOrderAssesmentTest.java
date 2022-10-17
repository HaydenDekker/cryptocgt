package com.hdekker.cryptocgt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.data.AccountOrderSnapshot;
import com.hdekker.cryptocgt.data.TransactionType;
import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.interfaces.AccountOrdersAssesment;

@SpringBootTest
public class AccountOrderAssesmentTest {

	Order testOrder1() {
		
		Order o = new Order(
			LocalDateTime.of(2017, 5, 21, 16, 51),
			TransactionType.Sell,
			"BTC/ETH",
			0.01,
			28.31,
			"0.16363634 ETH"
			);
		
		return o;
		
	}
	
	Order testOrder2() {
		
		Order o = new Order(
				LocalDateTime.of(2017, 12, 10, 20, 27),
				TransactionType.Buy,
				"BTC/AUD",
				0.0046118,
				95.00,
				"95.00 AUD"
				);
		
		return o;
		
	}
	
	@Test
	public void getPrimaryOrSecondaryCoinNameTest() {
		String btcaud = "BTC/AUD";
		BiFunction<String, Boolean, String> fn = AccountOrdersAssesment.getPrimaryOrSecondaryCoinName();
		assertTrue(fn.apply(btcaud, true).equals("BTC"));
		assertTrue(fn.apply(btcaud, false).equals("AUD"));
	}
	
	@Test
	public void getAmountPrimary() {
		
		Order order = testOrder1();
		Double pr = AccountOrdersAssesment.calcAmountPrimary().apply(order);
		assertTrue(pr.equals(-0.01));
		
	}
	
	@Test
	public void getAmountSecondary() {
		
		Order o = testOrder1();
		assertTrue(AccountOrdersAssesment.calcAmountSecondary().apply(o).equals(0.16363634));
		
	}

	@Test
	public void calcprimaryExchangeRateInAUD() {
		
		Order o = testOrder1();
		assertTrue(AccountOrdersAssesment.calcPrimaryExchangeRateAUD().apply(o).equals(2831.0));
		
	}
	
	@Test
	public void clacSecondaryExchangeRateAUDTest() {
		
		Order o = testOrder1();
		Double val = AccountOrdersAssesment.calcSecondaryExchangeRateAUD().apply(o);
		assertTrue(val.equals(173.00558054525052));
		
	}
	
	
	
	@Test
	public void createOrderSnapshotTest() {
		
		AccountOrderSnapshot aos = AccountOrdersAssesment.createOrderSnapshot().apply(testOrder1());
		assertTrue(aos.getCob1().getCoinName().equals("BTC"));
		assertTrue(aos.getCob2().getCoinName().equals("ETH"));
		assertTrue(aos.getCob1().getCoinAmount().equals(-0.01));
		assertTrue(aos.getCob2().getCoinAmount().equals(0.16363634));
		assertTrue(aos.getCob1().getExchangeRateAUD().equals(2831.0));
		assertTrue(aos.getCob2().getExchangeRateAUD().equals(173.00558054525052));
		
	}
	
	@Test
	public void createCOBForBuy() {
		
		AccountOrderSnapshot aos = AccountOrdersAssesment.createOrderSnapshot().apply(testOrder2());
		assertTrue(aos.getCob1().getCoinName().equals("BTC"));
		assertTrue(aos.getType().equals(TransactionType.Buy));
		assertTrue(aos.getCob1().getCoinAmount().equals(0.0046118));
	
	}
}
