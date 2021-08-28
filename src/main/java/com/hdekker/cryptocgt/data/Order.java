package com.hdekker.cryptocgt.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hdekker.cryptocgt.TransactionType;

/**
 * Transaction Date	Type	Market	Amount	Rate inc. fee	Rate ex. fee	Fee	Fee AUD (inc GST)	GST AUD	Total AUD	Total (inc GST)
20/5/2020 18:54	Buy	STEEM/AUD	63.3510153	0.473552	0.46886337	0.29702970 AUD	0.3	0.03	30	30.00 AUD

 * 
 * @author HDekker
 *
 */

public class Order {

	LocalDateTime transactionDate;
	TransactionType transactionType;
	String market;
	Double amount;
	Double rateIncFee;
	Double rateExFee;
	String fee;
	Double feeIncGST;
	Double gst;
	Double totalAUD;
	String totalIncGST;
	
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getRateIncFee() {
		return rateIncFee;
	}
	public void setRateIncFee(Double rateIncFee) {
		this.rateIncFee = rateIncFee;
	}
	public Double getRateExFee() {
		return rateExFee;
	}
	public void setRateExFee(Double rateExFee) {
		this.rateExFee = rateExFee;
	}
	
	public Double getFeeIncGST() {
		return feeIncGST;
	}
	public void setFeeIncGST(Double feeIncGST) {
		this.feeIncGST = feeIncGST;
	}
	public Double getGst() {
		return gst;
	}
	public void setGst(Double gst) {
		this.gst = gst;
	}
	public Double getTotalAUD() {
		return totalAUD;
	}
	public void setTotalAUD(Double totalAUD) {
		this.totalAUD = totalAUD;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getTotalIncGST() {
		return totalIncGST;
	}
	public void setTotalIncGST(String totalIncGST) {
		this.totalIncGST = totalIncGST;
	}
	
	
}
