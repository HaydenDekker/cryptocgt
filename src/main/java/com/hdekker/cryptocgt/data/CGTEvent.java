package com.hdekker.cryptocgt.data;

import java.time.LocalDateTime;

public class CGTEvent {

	final LocalDateTime disposedDate;
	final LocalDateTime purchasedDate;
	final Double cgt;
	final String coinName;
	
	public CGTEvent(LocalDateTime disposedDate, LocalDateTime purchasedDate, Double cgt, String coinName) {
		super();
		this.disposedDate = disposedDate;
		this.purchasedDate = purchasedDate;
		this.cgt = cgt;
		this.coinName = coinName;
	}
	public LocalDateTime getDisposedDate() {
		return disposedDate;
	}
	public LocalDateTime getPurchasedDate() {
		return purchasedDate;
	}
	public Double getCgt() {
		return cgt;
	}
	public String getCoinName() {
		return coinName;
	}
	
}
