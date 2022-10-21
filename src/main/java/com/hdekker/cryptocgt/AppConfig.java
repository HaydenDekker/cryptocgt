package com.hdekker.cryptocgt;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Calendar;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;

@Configuration
@ConfigurationProperties(prefix = "cryptocgt.config")
public class AppConfig {

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
	
	
	
}
