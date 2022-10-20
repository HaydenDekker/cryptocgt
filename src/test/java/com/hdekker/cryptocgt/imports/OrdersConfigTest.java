package com.hdekker.cryptocgt.imports;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.AppConfig;
import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.data.transaction.TransactionType;
import com.hdekker.cryptocgt.imports.coinspot.OrdersCSVExtractor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class OrdersConfigTest {

	Logger log = LoggerFactory.getLogger(OrdersConfigTest.class);
	
	@Autowired
	OrdersCSVExtractor ordersExtractor;
	
	@Autowired
	AppConfig appConfig;
	
	@Test
	public void importsOrders() throws Exception {
		
		log.info("The test csv file url is " + appConfig.getBuysSellsCSV());
		
		BufferedReader reader = CSVUtils.openDocumentReader()
			.apply(appConfig.getBuysSellsCSV())
			.get();
		
		List<Order> orders = ordersExtractor.getOrders(reader);	
				
		
		assertThat(orders.size(), equalTo(1));
		
		Order order = orders.get(0);
		assertThat(
				// 24/08/2021  10:46:00 AM
				order.getTransactionDate(), 
				equalTo(LocalDateTime.of(2021, 8, 24, 10, 46)));
		assertThat(order.getTransactionType(), equalTo(TransactionType.Sell));
		assertThat(order.getAmount(), equalTo(0.19952114));
		assertThat(order.getMarket(), equalTo("UNI/AUD"));
		assertThat(order.getTotalAUD(), equalTo(7.82));
		assertThat(order.getTotalIncGST(), equalTo("7.82122869 AUD"));
		
	}
	
}
