package com.hdekker.cryptocgt.data;

public class CoinBalance {

	final String coinName;
	final Double coinAmount;
	
	public CoinBalance(String coinName, Double coinAmount) {
		super();
		this.coinName = coinName;
		this.coinAmount = coinAmount;
	}
	public String getCoinName() {
		return coinName;
	}
	public Double getCoinAmount() {
		return coinAmount;
	}

}
