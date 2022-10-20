package com.hdekker.cryptocgt.data.transaction;

import java.time.LocalDateTime;

public class SendRecieves {

	final String coin;
	final Double amount;
	final LocalDateTime transactionDate;
	final TransactionType type;
	final Double exchangeRateAUD;
	
	public SendRecieves(String coin, Double amount, LocalDateTime transactionDate, TransactionType type,
			Double exchangeRateAUD) {
		super();
		this.coin = coin;
		this.amount = amount;
		this.transactionDate = transactionDate;
		this.type = type;
		this.exchangeRateAUD = exchangeRateAUD;
	}
	public Double getExchangeRateAUD() {
		return exchangeRateAUD;
	}
	public String getCoin() {
		return coin;
	}
	public Double getAmount() {
		return amount;
	}
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public TransactionType getType() {
		return type;
	}
	
}
