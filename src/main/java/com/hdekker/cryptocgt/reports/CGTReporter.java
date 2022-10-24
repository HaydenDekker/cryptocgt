package com.hdekker.cryptocgt.reports;

import java.io.FileWriter;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdekker.cryptocgt.AppConfig;
import com.hdekker.cryptocgt.data.CGTEvent;

/**
 * Creates report for each tax year.
 * CGT events are ordered by date.
 * The total CGT amount is calculated
 * across all assets.
 * 
 * 
 * @param assetCGTEvents
 * @return
 */
@Component
public class CGTReporter {
	
	Logger log = LoggerFactory.getLogger(CGTReporter.class);

	ObjectMapper om = new ObjectMapper();
	
	FileWriter fw = null;
	
	@Autowired
	AppConfig appConfig;
	
	public CGTReporter() {
		om.registerModule(new JavaTimeModule());
	}
	
	private Double summCGT(List<CGTEvent> events){
		
		return events
			.stream()
			.map(c->c.getCgt())
			.reduce((a,b)-> a+b)
			.get();
	
	}
	
	private Predicate<CGTEvent> isEventDiscountable = (cgt) ->{
		return cgt.getDisposedDate().minusYears(1)
				.compareTo(cgt.getPurchasedDate()) >= 0;
	};
	
	Double calculateDiscount(List<CGTEvent> events){
		
		return events.stream()
					.filter(e->
						isEventDiscountable.test(e)
					).map(e->e.getCgt())
					.reduce((a, b)-> a+b)
					.orElse(0.0);
		
	}
	
	@Autowired
	AssetToTaxYearMapper mapper;
	
	public List<CGTTaxReport> createReport(List<CGTEvent> assetCGTEvents){

		Map<Year, List<CGTEvent>> eventsByYear = mapper.mapByYear(assetCGTEvents, CGTEvent::getDisposedDate);
		
		return eventsByYear.entrySet()
					.stream()
					.map(es-> {
						
					 	List<CGTEvent> events = eventsByYear.get(es.getKey());
					
						return new CGTTaxReport(
								es.getKey(), 
								events, 
								summCGT(events),
								calculateDiscount(events));
					})
					.collect(Collectors.toList());
	}
	
}
