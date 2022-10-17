package com.hdekker.cryptocgt.imports;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.AppConfig;

import reactor.util.function.Tuple2;

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
	
	/**
	 *  Needs top open document as buffered reader but
	 *  get the first line as headings. 
	 *  
	 *  This is irrespective of CSV.
	 * 
	 */
	@Test
	public void uGetsColumnsHeadings() {
		
		StringReader r = new StringReader("CSV H1, CSV H2");
		BufferedReader br = new BufferedReader(r);
		Function<BufferedReader, List<String>> fn = CSVUtils.getColumnHeadings();
		List<String> headings = fn.apply(br);
		assertThat(headings.size(), equalTo(2));
		assertThat(headings.get(0), equalTo("CSV H1"));
		
	}
	
	/**
	 * And check that headings
	 *  are as expected.
	 * 
	 */
	@Test
	public void uContainsRequiredHeadings() {
		
		List<String> req = Arrays.asList("1", "2", "3");
		List<String> act1 = Arrays.asList("1", "2", "3");
		List<String> act2 = Arrays.asList("1", "2");
		BiPredicate<List<String>, List<String>> fn = CSVUtils.hasRequiredHeadings;
		assertThat(fn.test(req, act1), equalTo(true));
		assertThat(fn.test(req, act2), equalTo(false));
		
	}
	
	/**
	 *  Need to put your test doc in app properties.
	 *  
	 * 
	 */
	@Test
	public void itOpensTestDocAndGetsHeadings() {
		
		Function<String, Tuple2<BufferedReader, List<String>>> fn = CSVUtils.openDocumentAndGetHeadings;
		Tuple2<BufferedReader, List<String>> out = fn.apply(userConfig.getSendsReceivesCSV());
		assertThat(out.getT1(), notNullValue());
		assertThat(out.getT2().get(0), equalTo("Transaction Date"));
	
	}
	
	/**
	 *  TODO excell format requirements
	 *  TODO how to manage this?? Maybe 
	 *  ppl need to insert expression.
	 *  Or test that it will work. thats better.
	 * 
	 */
	@Test
	public void uConvertsTimeFormat() {
		
		String timeString = "24/8/2021  10:43"; 
		Function<String, LocalDateTime> fn = CSVUtils.Converters.dateTimeConverter;
		assertThat(fn.apply(timeString), equalTo(LocalDateTime.of(2021, 8, 24, 10, 43, 0, 0)));
		
	}
	
}
