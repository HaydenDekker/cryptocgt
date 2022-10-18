package com.hdekker.cryptocgt.data;

import java.time.LocalDateTime;

public class SendReceiveEvent {

	// Transaction Date	Type	Coin	Status	Fee	Amount
	final LocalDateTime transactionDate;
	final String transactionType;
	final String coinName;
	final Double amount;
	
	public SendReceiveEvent(LocalDateTime transactionDate, String transactionType, String coinName, Double amount) {
		super();
		this.transactionDate = transactionDate;
		this.transactionType = transactionType;
		this.coinName = coinName;
		this.amount = amount;
	}
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public String getCoinName() {
		return coinName;
	}
	public Double getAmount() {
		return amount;
	}
	
}
