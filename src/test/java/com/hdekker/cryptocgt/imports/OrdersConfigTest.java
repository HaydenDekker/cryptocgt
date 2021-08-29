package com.hdekker.cryptocgt.imports;

import java.io.BufferedReader;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.UserConfig;
import com.hdekker.cryptocgt.data.Order;

import reactor.util.function.Tuple2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class OrdersConfigTest {

	@Autowired
	UserConfig userConfig;
	
	@Test
	public void hasRequiredHeadings() {
		// TODO implement
	}
	
	/**
	 *  Need to change values here to 
	 *  ones specific to your configured report
	 * 
	 */
	@Test
	public void itImportsOrders() {
		
		Tuple2<BufferedReader, List<String>> input = CSVUtils.openDocumentAndGetHeadings
				.apply(userConfig.getBuysSellsCSV());
		
		Function<Tuple2<BufferedReader, List<String>>, List<Order>> fn = OrdersConfig.getOrders();
		
		List<Order> out = fn.apply(input);
		
		assertThat(out.size(), equalTo(963));
		
	}
	
}
