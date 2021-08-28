package com.hdekker.cryptocgt.data;

import java.time.LocalDateTime;

public class CGTEvent {

	LocalDateTime disposedDate;
	LocalDateTime purchasedDate;
	Double cgt;
	String coinName;
	
	public LocalDateTime getDisposedDate() {
		return disposedDate;
	}
	public void setDisposedDate(LocalDateTime disposedDate) {
		this.disposedDate = disposedDate;
	}
	public LocalDateTime getPurchasedDate() {
		return purchasedDate;
	}
	public void setPurchasedDate(LocalDateTime purchasedDate) {
		this.purchasedDate = purchasedDate;
	}
	public Double getCgt() {
		return cgt;
	}
	public void setCgt(Double cgt) {
		this.cgt = cgt;
	}
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	
}
