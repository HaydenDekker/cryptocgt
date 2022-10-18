package com.hdekker.cryptocgt;


import static com.hdekker.cryptocgt.orders.AccountOrdersAssesment.*;

import java.util.List;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.cryptocgt.data.AccountOrderSnapshot;
import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.imports.OrdersCSVExtractor;
import com.hdekker.cryptocgt.imports.SendRecieveConfig;

public class CGTCalculator {

	Logger log = LoggerFactory.getLogger(CGTCalculator.class);
	
	@Autowired
	SendRecieveConfig sendRecieveConfig;
	
	@Autowired
	OrdersCSVExtractor ordersConfig;
	
	public CGTCalculator() {
		
		// TODO wahh
		log.info("Preparing order history ... not started ");
//
//		List<Order> allOrders = ordersConfig.getOrders();
//		log.info("Extracted " + allOrders.size() + " of orders.");
//		
//		// create balance snapshots at each order.
//		List<Order> filteredByDate = allOrders.stream().sorted((a,b)-> {
//			return a.getTransactionDate().compareTo(b.getTransactionDate());
//		}).collect(Collectors.toList());
//		
//		log.info("The first order was on " + filteredByDate.get(0).getTransactionDate());
//		
//		// create snaps
//		List<AccountOrderSnapshot> aoss = filteredByDate.stream()
//											.map(order -> createOrderSnapshot().apply(order))
//											.collect(Collectors.toList());
//		
		///List<CoinBalance> btcTrans = aoss.stream().filter(s->s)
		
		// - start with two lists, sends/received and orders.
		// - start with earliest date and make a balance snapshot
		// - find the next date and make another balance snapshot
		//  - balance snapshot will be as a result of the send/recieved or orders.
		
		// calculate cgt event for each order.
		
		// reduce cgt events for net capital gain.
		
	}
	
	
	
//	private List<Order> createOrders(BufferedReader br, List<String> columnOrder) {
//		
//		return br.lines()
//		.map(line-> splitByCommas().apply(line))
//		.map(values -> 
//		{
//			Order order = new Order();
//			
//			for(int i = 0; i<columnOrder.size(); i++) {
//				
//				order = objectSetters.get(columnOrder.get(i))
//						.apply(order, values.get(i));
//
//			}
//				
//			return order;
//			
//		})
//		.collect(Collectors.toList());
//		
//	}

	
	
}
