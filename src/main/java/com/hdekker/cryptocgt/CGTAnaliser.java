package com.hdekker.cryptocgt;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.cgt.CGTCalculator;
import com.hdekker.cryptocgt.cgt.CGTUtils;
import com.hdekker.cryptocgt.data.CGTEvent;
import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.reports.CGTReporter;
import com.hdekker.cryptocgt.reports.CGTTaxReport;

@Component
public class CGTAnaliser {

	Logger log = LoggerFactory.getLogger(CGTAnaliser.class);
	
	
	@Autowired
	AppConfig appConfig;
	
	@Autowired
	CGTUtils cgtUtils;
	
	@Autowired
	CGTReporter reporter;
	
	@Autowired
	CGTCalculator cgtCalculator;
	
	
	public List<CGTTaxReport> analyiseCGT(
			List<AssetBalance> assetBalances) throws Exception {
		
		// This is simply a view into the data
		Map<String, List<AssetBalance>> map = cgtUtils.mapAssetBalancesByAssetName(
				assetBalances);
		
		// Calculate CGT on a per coin basis
		List<CGTEvent> cgts =cgtCalculator.calculateCGTByAsset(map);
		
		// reporting filter into tax years
		return reporter.createReport(cgts);
		
	}
	
}
