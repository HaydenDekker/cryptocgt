package com.hdekker.cryptocgt.reports;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.AppConfig;
import com.hdekker.cryptocgt.data.CGTEvent;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest
public class CGTReporterTest {
	
	Logger log = LoggerFactory.getLogger(CGTReporterTest.class);
	
	@Autowired
	CGTReporter cgtReporter;
	
	@Autowired
	AssetToTaxYearMapper yearMapper;
	
	public List<CGTEvent> getEvents(){
		return 
				List.of(
					new CGTEvent(
						LocalDateTime.now().minusDays(1),
						LocalDateTime.now().minusDays(2),
						1.5,
						"test-asset")
				);
	}
	
	public Year getYearForTestEvents() {
		return yearMapper.getYear(LocalDateTime.now());
	}
	
	@Autowired
	AppConfig config;
	
	@Test
	public void appConfig() {
		
		assertThat(config.getBuysSellsCSV(), notNullValue());
		assertThat(config.getTaxYearEnd().get(Calendar.MONTH), equalTo(5));
	}

	@Test
	public void producesReport() {
		
		List<CGTTaxReport> report = cgtReporter.createReport(getEvents());
		assertThat(report.size(), equalTo(1));
		assertThat(report.get(0).getCgtTotal(), equalTo(1.5));
		assertThat(report.get(0).getTaxYear(), equalTo(getYearForTestEvents()));
	}
	
}
