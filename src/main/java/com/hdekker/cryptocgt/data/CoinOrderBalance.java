package com.hdekker.cryptocgt.data;

import java.time.LocalDateTime;

// order always has a from and two but with an exchange rate
public class CoinOrderBalance extends CoinBalance{

	Double exchangeRateAUD;
	
	public Double getExchangeRateAUD() {
		return exchangeRateAUD;
	}
	public void setExchangeRateAUD(Double exchangeRateAUD) {
		this.exchangeRateAUD = exchangeRateAUD;
	}
	
	LocalDateTime balanceDate;

	public LocalDateTime getBalanceDate() {
		return balanceDate;
	}
	public void setBalanceDate(LocalDateTime balanceDate) {
		this.balanceDate = balanceDate;
	}
	
	
	
}
