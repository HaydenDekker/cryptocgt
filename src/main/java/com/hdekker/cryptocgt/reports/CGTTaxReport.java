package com.hdekker.cryptocgt.reports;

import java.time.Year;
import java.util.List;
import com.hdekker.cryptocgt.data.CGTEvent;

/**
 * The report of CGT events for a tax year.
 * 
 * @author Hayden Dekker
 *
 */
public class CGTTaxReport {

	final Year taxYear;
	final List<CGTEvent> events;
	final Double capitalGain;
	final Double cgtTotal;
	final Double discountsTotal;
	
	public CGTTaxReport(Year taxYear, 
			List<CGTEvent> events,
			Double capitalGain,
			Double cgtTotal,
			Double discountsTotal) {
		super();
		this.taxYear = taxYear;
		this.events = events;
		this.capitalGain = capitalGain;
		this.cgtTotal = cgtTotal;
		this.discountsTotal = discountsTotal;
	}
	
	public Double getCapitalGain() {
		return capitalGain;
	}

	public Double getDiscountsTotal() {
		return discountsTotal;
	}

	public Year getTaxYear() {
		return taxYear;
	}
	public List<CGTEvent> getEvents() {
		return events;
	}
	public Double getCgtTotal() {
		return cgtTotal;
	}
	
}
