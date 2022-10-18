package com.hdekker.cryptocgt.data;

import java.time.LocalDateTime;

// order always has a from and two but with an exchange rate
public class CoinOrderBalance extends CoinBalance{

	final Double exchangeRateAUD;
	
	public Double getExchangeRateAUD() {
		return exchangeRateAUD;
	}

	final LocalDateTime balanceDate;

	public LocalDateTime getBalanceDate() {
		return balanceDate;
	}

	public CoinOrderBalance(String coinName, Double coinAmount, Double exchangeRateAUD, LocalDateTime balanceDate) {
		super(coinName, coinAmount);
		this.exchangeRateAUD = exchangeRateAUD;
		this.balanceDate = balanceDate;
	}
	
	
}
