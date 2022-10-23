package com.hdekker.cryptocgt.reports;

import java.io.FileWriter;
import java.time.Year;
import java.util.List;
import java.util.Map;
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
	
	private Map<Year, Double> summCGT(Map<Year, List<CGTEvent>> events){
		
		return events
		.entrySet()
		.stream()
		.collect(Collectors.toMap(
				k->k.getKey(), v->{
					
					return v.getValue()
						.stream()
						.map(c->c.getCgt())
						.reduce((a,b)-> a+b)
						.get();

				}));
		
	}
	
	@Autowired
	AssetToTaxYearMapper mapper;
	
	public List<CGTTaxReport> createReport(List<CGTEvent> assetCGTEvents){

		Map<Year, List<CGTEvent>> eventsByYear = mapper.mapByYear(assetCGTEvents, CGTEvent::getDisposedDate);
		
		Map<Year, Double> summedCGTByYear = summCGT(eventsByYear);
		
		return summedCGTByYear.entrySet()
					.stream()
					.map(es->
						new CGTTaxReport(
								es.getKey(), 
								eventsByYear.get(es.getKey()), 
								es.getValue())
					)
					.collect(Collectors.toList());
	}
	
}
