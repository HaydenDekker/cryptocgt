package com.hdekker.cryptocgt.imports;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.AppConfig;
import com.hdekker.cryptocgt.data.TransactionType;
import com.hdekker.cryptocgt.data.transaction.SendRecieves;
import com.hdekker.cryptocgt.imports.SendRecieveConfig;

import reactor.util.function.Tuple2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class SendRecieveConfigTest {

	@Autowired
	AppConfig userConfig;
	
	@Autowired
	SendRecieveConfig sendReceiveExtractor;
	
	/**
	 * Requires application properties to be set.
	 * TODO should be specifying test app properties
	 * Opens and imports sends received.
	 * @throws Exception 
	 * 
	 */
	@Test
	public void importsSendsRecieves() throws Exception {

		BufferedReader reader = CSVUtils.openDocumentReader()
			.apply(userConfig.getSendsReceivesCSV())
			.get();
		
		List<SendRecieves> srs = sendReceiveExtractor.getSendRecieves(reader);
		
		assertThat(srs.size(), equalTo(1));
		SendRecieves sr = srs.get(0);
		assertThat(sr.getAmount(), equalTo(2.07273632));
		assertThat(sr.getCoin(), equalTo("ARG"));
		assertThat(sr.getExchangeRateAUD(), equalTo(223.09));
		assertThat(sr.getTransactionDate(), equalTo(LocalDateTime.of(2018, 1, 25, 14, 58)));
		assertThat(sr.getType(), equalTo(TransactionType.Receive));
		
	}
	
}
