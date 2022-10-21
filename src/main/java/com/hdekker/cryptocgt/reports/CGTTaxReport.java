package com.hdekker.cryptocgt.reports;

import java.time.LocalDateTime;
import java.util.List;
import com.hdekker.cryptocgt.data.CGTEvent;

/**
 * The report of CGT events for a tax year.
 * 
 * @author Hayden Dekker
 *
 */
public class CGTTaxReport {

	final LocalDateTime from;
	final LocalDateTime to;
	final List<CGTEvent> events;
	final Double cgtTotal;
	
	public CGTTaxReport(LocalDateTime from, LocalDateTime to, List<CGTEvent> events,
			Double cgtTotal) {
		super();
		this.from = from;
		this.to = to;
		this.events = events;
		this.cgtTotal = cgtTotal;
	}
	public LocalDateTime getFrom() {
		return from;
	}
	public LocalDateTime getTo() {
		return to;
	}
	public List<CGTEvent> getEvents() {
		return events;
	}
	public Double getCgtTotal() {
		return cgtTotal;
	}
	
}
