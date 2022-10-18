package com.hdekker.cryptocgt;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdekker.cryptocgt.data.CGTEvent;
import com.hdekker.cryptocgt.data.CoinOrderBalance;
import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.data.transaction.SendRecieves;
import com.hdekker.cryptocgt.data.transaction.TransactionType;
import com.hdekker.cryptocgt.imports.CSVUtils;
import com.hdekker.cryptocgt.imports.OrdersCSVExtractor;
import com.hdekker.cryptocgt.imports.SendRecieveConfig;
import com.hdekker.cryptocgt.interfaces.CGTUtils;

import reactor.util.function.Tuple2;

import com.hdekker.cryptocgt.imports.CSVUtils.Converters;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
public class CGTEventTest {
	
	Logger log = LoggerFactory.getLogger(CGTEventTest.class);
	
	Order buildOrder(String date, TransactionType type, String market, String amount, String totalAUD, String totalIncGST) {
		
		Order o = new Order(
				Converters.dateTimeConverter.apply(date),
				type,
				market,
				Double.valueOf(amount),
				Double.valueOf(totalAUD),
				totalIncGST
				);
		
		return o;
		
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
	List<Order> testOrders() {

		return Arrays.asList(
				// 4th is a CGT event.. sells parts of the most transaction being the item on the 13th.
				buildOrder("3/7/2017 13:46", TransactionType.Sell, "ETH/EOS", "0.015", "5.4", "1.26932232 EOS"),
				// 3rd is a CGT event.. sells part of most recent transaction being 10 minutes before.
				buildOrder("13/6/2017 17:31", TransactionType.Sell, "ETH/GNT", "0.06", "31.97", "42.00726474 GNT"),
				// 2nd but.. no CGT event. Accumulated ETH = .16363634 + .09898585
				buildOrder("13/6/2017 17:22", TransactionType.Sell, "BTC/ETH", "0.014", "52.37", "0.09898585 ETH"),
				// initial.. no CGT event
				buildOrder("21/5/2017 16:51", TransactionType.Sell, "BTC/ETH", "0.01", "28.31", "0.16363634 ETH"));
	}
	
	
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
	public void uCreateAMapOfCoinOrderBalancesForCGTCalculations() {
		
		// TODO why did I put sort orig
		Function<List<Order>, List<CoinOrderBalance>> cob = CGTUtils.getCoinBalancesForOrders();
		Function<Function<CoinOrderBalance, String>, Function<List<CoinOrderBalance>, Map<String, List<CoinOrderBalance>>>> orderBalanceMapFn = CGTUtils.convertToHashMapAndSortByKeyValue();
		List<CoinOrderBalance> list = cob.apply(testOrders());
		Map<String, List<CoinOrderBalance>> map = orderBalanceMapFn
													.apply(CoinOrderBalance::getCoinName)
													.apply(list);
		assertTrue(map.keySet().size()==4);
		assertTrue(map.get("BTC").size()==2);
		assertTrue(map.get("ETH").size()==4);
		
		
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
	
	/**
	 * 
	 * The order snapshot may or may not cause CGT events,
	 * Initial purchasing doesn't cause CGT events only Disposals.
	 * 
	 * All Buys are initial purchases.
	 * The initial Receives can be considered buys too.
	 * 
	 * CGT Events relate to just a signle asset.
	 * No need to consider other assets in the calculation.
	 * 
	 * 
	 */
	
	
	/**
	 * 
	 */
	@Test
	public void uMostRecentIsFound() {
		
		CoinOrderBalance cob1 = new CoinOrderBalance(
				null,
				null,
				null,
				LocalDateTime.now()
				);
		
		CoinOrderBalance cob2 = new CoinOrderBalance(
				null,
				null,
				null,
				LocalDateTime.now().minusDays(1)
				);

		CoinOrderBalance cob3 = new CoinOrderBalance(
				null,
				null,
				null,
				LocalDateTime.now().minusDays(2)
				
				);
		
		CoinOrderBalance cob4 = new CoinOrderBalance(
				null,
				null,
				null,
				LocalDateTime.now().plusDays(2));

		BiFunction<CoinOrderBalance, List<CoinOrderBalance>, CoinOrderBalance> fn = CGTUtils.findTheMostRecentPurchase();
		CoinOrderBalance cob = fn.apply(cob1, Arrays.asList(cob3, cob2, cob4));
		assertTrue(cob.equals(cob2));
		
	}
	

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
	
	// finally sum CGT events
//	@Test
//	public void itDetectsAndSummsCorrectCGTEvent() {
//		
//		BiFunction<List<Order>, List<SendRecieves>, Map<String, List<CoinOrderBalance>>> fn = CGTUtils.combineAndMapOrdersAndSendRecievesToCobs();
//		
//		// orders
//		Tuple2<BufferedReader, List<String>> orderInput = CSVUtils.openDocumentAndGetHeadings
//				.apply(userConfig.getBuysSellsCSV());
//		
//		Function<Tuple2<BufferedReader, List<String>>, List<Order>> orderfn = OrdersCSVExtractor.getOrders();
//		
//		// sends recieves
//		Tuple2<BufferedReader, List<String>> srinput = CSVUtils.openDocumentAndGetHeadings
//				.apply(userConfig.getSendsReceivesCSV());
//
//		Function<Tuple2<BufferedReader, List<String>>, List<SendRecieves>> srfn = 
//				SendRecieveConfig.getSendRecieves();
//
//		Map<String, List<CoinOrderBalance>> map = fn
//													.apply(orderfn.apply(orderInput), srfn.apply(srinput));
//		
//		
//		
//		// TODO fix map list in top level
//		Function<String, List<CGTEvent>> cgtsFun = CGTUtils.getCGTUsingOrdersSendsAndRecieves(map);
//			
//		// computes CGT per coin
//		Map<String, List<CGTEvent>> cgts = map.keySet()
//									.stream()
//									.collect(Collectors.toMap((s)->s, 
//											(s) -> {
//												log.info("Starting " + s);
//												return cgtsFun.apply(s);	
//											}
//									));
//		
//		// plaint old events, why?
//		List<CGTEvent> flattened = cgts.entrySet().stream()
//												.map(entry-> entry.getValue())
//												.reduce((a,n)->{
//													List<CGTEvent> l = new ArrayList<>();
//													l.addAll(n);
//													l.addAll(a);
//													return l;
//												}).orElseThrow();
//		
//		// writes report ahh yuk.
//		ObjectMapper om = new ObjectMapper();
//		om.registerModule(new JavaTimeModule());
//		
//		try {
//			log.info(om.writeValueAsString(cgts));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		FileWriter fw = null;
//		
//		
//		try {
//			fw = new FileWriter(new File(userConfig.getReportLocation()));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		BufferedWriter writer = new BufferedWriter(fw);
//		
//		List<String> keys = cgts.keySet().stream().collect(Collectors.toList());
//		
//		for(int k = 0; k<keys.size(); k++) {
//			
//			List<CGTEvent> items = cgts.get(keys.get(k));
//			
//			for(int i = 0; i<items.size(); i++) {
//				try {
//					CGTEvent cgt = items.get(i);
//					writer.write(cgt.getCoinName() + "," + cgt.getCgt() + ",\"" + cgt.getDisposedDate() + "\",\"" + cgt.getPurchasedDate() + "\"");
//					writer.newLine();
//					
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//			Double sum17 = 0.0; 
//			Double sum18 = 0.0;
//			Double sum19 = 0.0;
//			Double sum20 = 0.0;
//			
//			try {
//				// todo silly
//				sum17 = CGTUtils.dateFilter(LocalDateTime.of(2017, 07, 01, 00, 00), LocalDateTime.of(2018, 06, 30, 23, 59)).andThen(CGTUtils.cgtSummer()).apply(items);
//				sum18 = CGTUtils.dateFilter(LocalDateTime.of(2018, 07, 01, 00, 00), LocalDateTime.of(2019, 06, 30, 23, 59)).andThen(CGTUtils.cgtSummer()).apply(items);
//				sum19 = CGTUtils.dateFilter(LocalDateTime.of(2019, 07, 01, 00, 00), LocalDateTime.of(2020, 06, 30, 23, 59)).andThen(CGTUtils.cgtSummer()).apply(items);
//				sum20 = CGTUtils.dateFilter(LocalDateTime.of(2020, 07, 01, 00, 00), LocalDateTime.of(2021, 06, 30, 23, 59)).andThen(CGTUtils.cgtSummer()).apply(items);
//						
//			}catch (Exception e) {
//				log.info("no value present for key " + keys.get(k));
//			}
//			
//			try {
//				writer.write(",,,,\"The total cgt balance for " + keys.get(k) + " is\"," + sum17 + "," + sum18 + "," + sum19 + "," + sum20);
//				writer.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
//		try {
//			writer.close();
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
////		log.info("Calculated " + cgts.size() + " events");
////		
////		log.info(cgts.stream().map(c->c.getCgt()).collect(Collectors.toList()).toString());
////		
////		log.info("The final cgt for "+ cgts.get(0).getCoinName() +" came to " + cgtSummer().apply(cgts));
////		
//	}
//	
//	// now go through each coin and print an ordered report
//	
}
