package com.hdekker.cryptocgt.imports;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.data.transaction.TransactionType;
import com.hdekker.cryptocgt.imports.coinspot.CoinspotOrderAssetBalanceConverter;

@SpringBootTest
public class CoinspotOrdersAssetBalanceConverterTest {

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
	public void splitsMarketString() {
		String btcaud = "BTC/AUD";
		List<String> list = CoinspotOrderAssetBalanceConverter.splitMarketString(btcaud);
		assertTrue(list.get(0).equals("BTC"));
		assertTrue(list.get(1).equals("AUD"));
	}
	
	@Test
	public void getAmountOfAssetSold() {
		
		Order order = testOrder1();
		Double pr = CoinspotOrderAssetBalanceConverter.calcAmountPrimary().apply(order);
		assertTrue(pr.equals(-0.01));
		
	}
	
	@Test
	public void getAmountOfAssetBought() {
		
		Order o = testOrder1();
		Double amt = CoinspotOrderAssetBalanceConverter.calcAmountSecondary().apply(o);
		assertTrue(amt.equals(0.16363634));
		
	}

	@Test
	public void calcprimaryExchangeRateInAUD() {
		
		Order o = testOrder1();
		assertTrue(CoinspotOrderAssetBalanceConverter.calcPrimaryExchangeRateAUD().apply(o).equals(2831.0));
		
	}
	
	@Test
	public void clacSecondaryExchangeRateAUDTest() {
		
		Order o = testOrder1();
		Double val = CoinspotOrderAssetBalanceConverter.calcSecondaryExchangeRateAUD().apply(o);
		assertTrue(val.equals(173.00558054525052));
		
	}
	
}
