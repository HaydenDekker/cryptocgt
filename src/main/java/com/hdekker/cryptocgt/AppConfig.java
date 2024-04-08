package com.hdekker.cryptocgt;

import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;

@Configuration
@ConfigurationProperties("cryptocgt.config")
public class AppConfig {
	
	Logger log = LoggerFactory.getLogger(AppConfig.class);

	String buysSellsCSV;
	String sendsReceivesCSV;
	String reportLocation;
	
	@DateTimeFormat(pattern = "dd-MM")
	Calendar taxYearBegin;
	
	@DateTimeFormat(pattern = "dd-MM")
	Calendar taxYearEnd;

	public Calendar getTaxYearBegin() {
		return taxYearBegin;
	}
	public void setTaxYearBegin(Calendar taxYearBegin) {
		this.taxYearBegin = taxYearBegin;
	}
	public Calendar getTaxYearEnd() {
		return taxYearEnd;
	}
	public void setTaxYearEnd(Calendar taxYearEnd) {
		this.taxYearEnd = taxYearEnd;
	}
	public String getBuysSellsCSV() {
		return buysSellsCSV;
	}
	public void setBuysSellsCSV(String buysSellsCSV) {
		this.buysSellsCSV = buysSellsCSV;
	}
	public String getSendsReceivesCSV() {
		return sendsReceivesCSV;
	}
	public void setSendsReceivesCSV(String sendsReceivesCSV) {
		this.sendsReceivesCSV = sendsReceivesCSV;
	}
	public String getReportLocation() {
		return reportLocation;
	}
	public void setReportLocation(String reportLocation) {
		this.reportLocation = reportLocation;
	}
	
	@PostConstruct
	public void log() {
		
		log.info(buysSellsCSV);
		log.info(sendsReceivesCSV);
		
	}
	
}
