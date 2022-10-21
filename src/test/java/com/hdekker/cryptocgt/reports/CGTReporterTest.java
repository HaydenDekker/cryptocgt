package com.hdekker.cryptocgt.reports;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.AppConfig;
import com.hdekker.cryptocgt.data.CGTEvent;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest
public class CGTReporterTest {
	
	@Autowired
	CGTReporter cgtReporter;
	
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
		
	}
	
}
