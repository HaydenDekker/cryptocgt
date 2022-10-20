package com.hdekker.cryptocgt.imports;

import java.io.BufferedReader;
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
	
}
