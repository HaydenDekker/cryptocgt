package com.hdekker.cryptocgt.cgt;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.AppConfig;
import com.hdekker.cryptocgt.data.CGTEvent;
import com.hdekker.cryptocgt.data.AssetBalance.BalanceType;
import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.data.transaction.TransactionType;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CGTUtilsTest {
	
	Logger log = LoggerFactory.getLogger(CGTUtilsTest.class);
	
	
	AssetBalance buildOrder(LocalDateTime date, TransactionType type, String market, String amount, String totalAUD, String totalIncGST) {
		
		Double er = Double.valueOf(totalIncGST)/Double.valueOf(amount);
		
		AssetBalance ab = new AssetBalance(
				market, 
				Double.valueOf(amount), 
				er, 
				date, 
				BalanceType.Transaction);
		
		return ab;
		
	}
	
	// 
	/*
	 * 
	  	- 3/7/2017 13:46	Sell	ETH/EOS	0.015	84.62148801	85.47625052	0.01282144 EOS	0.05	0	5.4	1.26932232 EOS
 		- 13/6/2017 17:31	Sell	ETH/GNT	0.06	700.121079	700.121079	0.00000000 GNT	0	0	31.97	42.00726474 GNT
		- 13/6/2017 17:22	Sell	BTC/ETH	0.014	7.07041801	7.07041801	0.00000000 ETH	0	0	52.37	0.09898585 ETH
		- 21/5/2017 16:51	Sell	BTC/ETH	0.01	16.36363366	16.36363366	0.00000000 ETH	0	0	28.31	0.16363634 ETH
	 * 
	 * 
	 */
	List<AssetBalance> testOrders() {

		return Arrays.asList(
				// 4th is a CGT event.. sells parts of the most transaction being the item on the 13th.
				buildOrder(LocalDateTime.of(2017, 7, 3, 13, 46), TransactionType.Sell, "ETH", "0.015", "5.4", "1.26932232"),
				// 3rd is a CGT event.. sells part of most recent transaction being 10 minutes before.
				buildOrder(LocalDateTime.of(2017, 6, 13, 17, 31), TransactionType.Sell, "ETH", "0.06", "31.97", "42.00726474"),
				// 2nd but.. no CGT event. Accumulated ETH = .16363634 + .09898585
				buildOrder(LocalDateTime.of(2017, 6, 13, 17, 22), TransactionType.Sell, "BTC", "0.014", "52.37", "0.09898585"),
				// initial.. no CGT event
				buildOrder(LocalDateTime.of(2017, 6, 21, 16, 51), TransactionType.Sell, "BTC", "0.01", "28.31", "0.16363634"));
	}
	
	
	@Autowired
	CGTUtils cgtUtils;

	/**
	 * 	CGT requires that we pay tax on capital but allows a discount
	 * 	for items held longer than 12 months.
	 * 
	 * Therefore its in our benefit to offset disposals against our most
	 * recent purchases, leaving the older purchases to continue the gain.....
	 * .. may not be the best way. Could choose a loss or a gain if 
	 * multiple purchases were made. Hmm.. bringing the profit forward or pushing
	 * it back.
	 * 
	 */
	@Test
	public void createAMapOfCoinOrderBalancesForCGTCalculations() {

		Map<String, List<AssetBalance>> map = cgtUtils.mapAssetBalancesByAssetName(testOrders());
		assertTrue(map.keySet().size()==2);
		assertTrue(map.get("BTC").size()==2);
		assertTrue(map.get("ETH").size()==2);
		
		
	}
	
	@Autowired
	AppConfig userConfig;
	
	
	/**
	 * Each CGT event requires subtracting the amount of the available buffer for a coin at a specific rate.
	 * Otherwise the earliest rate may be too great. 
	 * This requires a list of balances of unique buys for a coin with their rate at a date.
	 * As well as any sent recieves
	 * 
	 */
	
	// duplicate above really. 
//	@Test
//	public void createCumulativeBalanceList() {
//		
//		Map<String, List<CoinOrderBalance>> map = combineAndMapOrdersAndSendRecievesToCobs().apply(oConfig.getOrders(), srConfig.getSendRecieves(userConfig.getSendsReceivesCSV()));
//		
//		List<CoinOrderBalance> btcs = map.get("BTC");
//		ObjectMapper om = new ObjectMapper();
//		om.registerModule(new JavaTimeModule());
//		
//		try {
//			log.info(om.writeValueAsString(btcs));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	
	
	

	

	@Test
	public void dateFilterTest() {
		
		LocalDateTime testTime = LocalDateTime.of(2017, 8, 21, 23, 3);
		CGTEvent e = new CGTEvent(
				testTime,
				null,
				0.0,
				"");
		List<CGTEvent> list = CGTUtils.dateFilter(LocalDateTime.of(2017, 07, 01, 00, 00), LocalDateTime.of(2018, 06, 30, 23, 59)).apply(Arrays.asList(e));
		 assertTrue(list.get(0).equals(e));
		
	}
	
	
	// now go through each coin and print an ordered report
	
}
