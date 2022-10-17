package com.hdekker.cryptocgt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cryptocgt.config")
public class AppConfig {

	String buysSellsCSV;
	String sendsReceivesCSV;
	String reportLocation;
	
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
