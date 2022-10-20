package com.hdekker.cryptocgt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdekker.cryptocgt.cgt.CGTUtils;
import com.hdekker.cryptocgt.data.CGTEvent;
import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.reports.CGTTaxReport;

@Component
public class CGTCalculator {

	Logger log = LoggerFactory.getLogger(CGTCalculator.class);
	
	
	@Autowired
	AppConfig appConfig;
	
	@Autowired
	CGTUtils cgtUtils;
	
	
	public List<CGTTaxReport> calculateCGT(
			List<AssetBalance> assetBalances) throws Exception {
		
		// This is simply a view into the data
		Map<String, List<AssetBalance>> map = cgtUtils.mapAssetBalancesByAssetName(
				assetBalances);
		
		
		// TODO this is poor for paralisation, need to unravel
		Function<String, List<CGTEvent>> cgtsFun = CGTUtils.getCGTUsingOrdersSendsAndRecieves(map);
			
		// computes CGT per coin
		Map<String, List<CGTEvent>> cgts = map.keySet()
									.stream()
									.collect(Collectors.toMap((s)->s, 
											(s) -> {
												log.info("Starting " + s);
												return cgtsFun.apply(s);	
											}
									));
		
		// reporting
		
		// plaint old events, why?
		List<CGTEvent> flattened = cgts.entrySet().stream()
												.map(entry-> entry.getValue())
												.reduce((a,n)->{
													List<CGTEvent> l = new ArrayList<>();
													l.addAll(n);
													l.addAll(a);
													return l;
												}).orElseThrow();
		
		// writes report ahh yuk.
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(new File(appConfig.getReportLocation()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter writer = new BufferedWriter(fw);
		
		List<String> keys = cgts.keySet().stream().collect(Collectors.toList());
		
		for(int k = 0; k<keys.size(); k++) {
			
			List<CGTEvent> items = cgts.get(keys.get(k));
			
			for(int i = 0; i<items.size(); i++) {
				try {
					CGTEvent cgt = items.get(i);
					writer.write(cgt.getCoinName() + "," + cgt.getCgt() + ",\"" + cgt.getDisposedDate() + "\",\"" + cgt.getPurchasedDate() + "\"");
					writer.newLine();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Double sum17 = 0.0; 
			Double sum18 = 0.0;
			Double sum19 = 0.0;
			Double sum20 = 0.0;
			
			try {
				// todo silly
				sum17 = CGTUtils.dateFilter(LocalDateTime.of(2017, 07, 01, 00, 00), LocalDateTime.of(2018, 06, 30, 23, 59)).andThen(CGTUtils.cgtSummer()).apply(items);
				sum18 = CGTUtils.dateFilter(LocalDateTime.of(2018, 07, 01, 00, 00), LocalDateTime.of(2019, 06, 30, 23, 59)).andThen(CGTUtils.cgtSummer()).apply(items);
				sum19 = CGTUtils.dateFilter(LocalDateTime.of(2019, 07, 01, 00, 00), LocalDateTime.of(2020, 06, 30, 23, 59)).andThen(CGTUtils.cgtSummer()).apply(items);
				sum20 = CGTUtils.dateFilter(LocalDateTime.of(2020, 07, 01, 00, 00), LocalDateTime.of(2021, 06, 30, 23, 59)).andThen(CGTUtils.cgtSummer()).apply(items);
						
			}catch (Exception e) {
				log.info("no value present for key " + keys.get(k));
			}
			
			try {
				writer.write(",,,,\"The total cgt balance for " + keys.get(k) + " is\"," + sum17 + "," + sum18 + "," + sum19 + "," + sum20);
				writer.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		try {
			writer.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
