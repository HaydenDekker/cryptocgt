package com.hdekker.cryptocgt.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
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
import com.hdekker.cryptocgt.cgt.CGTUtils;
import com.hdekker.cryptocgt.data.CGTEvent;

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
	
	public List<CGTTaxReport> createReport(List<CGTEvent> assetCGTEvents){
		
		// Almost not interested in by asset over many years. Just interested in years and total of that year.
		// Assets should be sorted by date.
		

		
		return null;
	}
	
}
