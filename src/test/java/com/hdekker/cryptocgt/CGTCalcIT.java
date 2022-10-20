package com.hdekker.cryptocgt;

import java.io.BufferedReader;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.imports.CSVUtils;
import com.hdekker.cryptocgt.imports.CoinspotImporter;
import com.hdekker.cryptocgt.reports.CGTTaxReport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests component can receive
 * orders in coinspot format
 * 
 * and produce a detailed CGT report.
 * 
 * @author Hayden Dekker
 *
 */
@SpringBootTest
@ActiveProfiles({"personal"})
public class CGTCalcIT {

	@Autowired
	CGTCalculator calculator;
	
	@Autowired
	CoinspotImporter coinspotimporter;
	
	@Autowired
	AppConfig appConfig;
	
	private BufferedReader stubOrdersReader() {
		return CSVUtils.openDocumentReader()
				.apply(appConfig.getBuysSellsCSV())
				.get();
	}
	
	private BufferedReader stubSendReceivesReader() {
		return CSVUtils.openDocumentReader()
				.apply(appConfig.getSendsReceivesCSV())
				.get();
	}
	
	public static final Integer PERSONAL_DATA_TAX_YEARS_INCLUDED = 2;
	
	@Test
	public void producesReportFromCoinspotData() throws Exception {

		List<AssetBalance> assetBalances = coinspotimporter.importFromCoinSpot(
				stubOrdersReader(),
				stubSendReceivesReader());
		
		List<CGTTaxReport> report = calculator.calculateCGT(assetBalances);
		
		assertThat(report, notNullValue());
		assertThat(report.size(), greaterThan(0));

	}
	
}
