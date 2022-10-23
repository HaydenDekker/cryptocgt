package com.hdekker.cryptocgt.cgt.search.refactor;

import java.time.LocalDateTime;
import java.util.List;

import com.hdekker.cryptocgt.data.CGTEvent;

public class SearchQuery {
	
	final List<CGTEvent> eventsToSearch;
	final CGTEventDateSearchType searchType;
	final LocalDateTime relevantTime;
	
	public SearchQuery(List<CGTEvent> eventsToSearch, CGTEventDateSearchType searchType,
			LocalDateTime relevantTime) {
		super();
		this.eventsToSearch = eventsToSearch;
		this.searchType = searchType;
		this.relevantTime = relevantTime;
	}
	
	public List<CGTEvent> getEventsToSearch() {
		return eventsToSearch;
	}
	public CGTEventDateSearchType getSearchType() {
		return searchType;
	}
	public LocalDateTime getRelevantTime() {
		return relevantTime;
	}
	
}