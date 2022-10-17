package com.hdekker.cryptocgt.data.transaction;

import java.time.LocalDateTime;

import com.hdekker.cryptocgt.data.TransactionType;

/**
 * Transaction Date	Type	Market	Amount	Rate inc. fee	Rate ex. fee	Fee	Fee AUD (inc GST)	GST AUD	Total AUD	Total (inc GST)
20/5/2020 18:54	Buy	STEEM/AUD	63.3510153	0.473552	0.46886337	0.29702970 AUD	0.3	0.03	30	30.00 AUD

 * 
 * @author HDekker
 *
 */

public class Order {

	final LocalDateTime transactionDate;
	final TransactionType transactionType;
	final String market;
	final Double amount;
	final Double totalAUD;
	final String totalIncGST;
	
	public Order(
			LocalDateTime transactionDate, 
			TransactionType transactionType, 
			String market, 
			Double amount,
			Double totalAUD,
			String totalIncGST) {
		super();
		this.transactionDate = transactionDate;
		this.transactionType = transactionType;
		this.market = market;
		this.amount = amount;
		this.totalAUD = totalAUD;
		this.totalIncGST = totalIncGST;
	}
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public String getMarket() {
		return market;
	}
	public Double getAmount() {
		return amount;
	}
	public Double getTotalAUD() {
		return totalAUD;
	}
	public String getTotalIncGST() {
		return totalIncGST;
	}
	
}
