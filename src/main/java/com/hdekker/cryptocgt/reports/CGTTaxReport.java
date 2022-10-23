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
	final Double cgtTotal;
	
	public CGTTaxReport(Year taxYear, List<CGTEvent> events,
			Double cgtTotal) {
		super();
		this.taxYear = taxYear;
		this.events = events;
		this.cgtTotal = cgtTotal;
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
