package com.hdekker.cryptocgt.imports;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.UserConfig;
import com.hdekker.cryptocgt.imports.SendRecieveConfig;
import com.hdekker.cryptocgt.imports.SendRecieves;

import reactor.util.function.Tuple2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class SendRecieveConfigTest {

	@Autowired
	UserConfig userConfig;
	
	
	@Test
	public void uConfiguredDocumentHasRequiredHeadings() {
		
		Predicate<List<String>> fn = SendRecieveConfig.hasRequiredColumns;
		
		List<String> testString = Arrays.asList("Transaction Date","Type","Coin", "Status", "Fee", "Amount", "Address", "Txid", "Aud", "Ex AUD rate");
		assertTrue(fn.test(testString));
		
	}
	
	/**
	 * Requires application properties to be set.
	 * TODO should be specifying test app properties
	 * Opens and imports sends received.
	 * 
	 */
	@Test
	public void itImportSendsRecieves() {

		Tuple2<BufferedReader, List<String>> input = CSVUtils.openDocumentAndGetHeadings
				.apply(userConfig.getSendsReceivesCSV());

		Function<Tuple2<BufferedReader, List<String>>, List<SendRecieves>> fn = 
				SendRecieveConfig.getSendRecieves();

		List<SendRecieves> out = fn.apply(input);

		assertThat(out.size(), equalTo(12));
		assertThat(out.get(0).getExchangeRateAUD(), equalTo(223.09));
		
	}
	
}
