package com.hdekker.cryptocgt;

import static com.hdekker.cryptocgt.interfaces.CoinPriceCheck.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;
import java.util.function.Function;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.hdekker.cryptocgt.interfaces.CSVUtils.Converters;

import reactor.core.publisher.Mono;

@SpringBootTest
public class CoinPriceCheckTest {

	Logger log = LoggerFactory.getLogger(CoinPriceCheckTest.class);
	
	@Test
	public void getCoinvalueAtDate() {
		
		String testDateBTC = "21/5/2017 12:45";
		String testDateLTC = "24/1/2018 22:24";
		String testDateRipple = "15/12/2017 8:14";
		
		Mono<ResponseEntity<String>> btc = getCoinPriceAtDate("BTC").apply(Converters.dateTimeConverter.apply(testDateBTC));
		log.info("The AUD value of BTC at " + testDateBTC + " is " + extractMarketAmount().apply(btc.block().getBody()));

	}
	
//	@Test
//	public void 
//	
}
