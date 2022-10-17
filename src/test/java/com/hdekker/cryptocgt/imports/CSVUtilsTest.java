package com.hdekker.cryptocgt.imports;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.AppConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class CSVUtilsTest {

	@Autowired
	AppConfig userConfig;
	
	/**
	 *  Development Only - can access and open document
	 * 
	 */
	@Test
	public void opensConfigurationDocuments() {
		
		Function<String, Optional<BufferedReader>> fn = CSVUtils.openDocumentReader();
		
		assertThat(fn.apply(userConfig.getBuysSellsCSV()).isPresent(), equalTo(true));
		assertThat(fn.apply(userConfig.getSendsReceivesCSV()).isPresent(), equalTo(true));
		
	}
	
	@Test
	public void uConvertsTimeFormat() {
		
		String timeString = "24/8/2021  10:43"; 
		Function<String, LocalDateTime> fn = CSVUtils.Converters.dateTimeConverter;
		assertThat(fn.apply(timeString), equalTo(LocalDateTime.of(2021, 8, 24, 10, 43, 0, 0)));
		
	}
	
}
