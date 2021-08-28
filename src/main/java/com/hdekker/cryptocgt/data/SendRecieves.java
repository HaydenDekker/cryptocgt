package com.hdekker.cryptocgt.data;

import java.time.LocalDateTime;

import com.hdekker.cryptocgt.TransactionType;

public class SendRecieves {

	String coin;
	Double amount;
	LocalDateTime transactionDate;
	TransactionType type;
	Double exchangeRateAUD;
	
	
	public Double getExchangeRateAUD() {
		return exchangeRateAUD;
	}
	public void setExchangeRateAUD(Double exchangeRateAUD) {
		this.exchangeRateAUD = exchangeRateAUD;
	}
	public String getCoin() {
		return coin;
	}
	public void setCoin(String coin) {
		this.coin = coin;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	
	
	
}
