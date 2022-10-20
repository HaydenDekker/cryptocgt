package com.hdekker.cryptocgt.data;

import java.time.LocalDateTime;

/**
 * The blanace of an asset
 * at a point in time.
 * 
 * @author Hayden Dekker
 *
 */
public class AssetBalance{

	final String assetName;
	final Double assetAmount;
	final BalanceType balanceType;
	final LocalDateTime balanceDate;
	final Double exchangeRateAUD;
	
	public AssetBalance(
			String assetName, 
			Double assetAmount,
			Double exchangeRateAUD,
			LocalDateTime balanceDate,
			BalanceType balanceType) {
		
		this.assetName = assetName;
		this.assetAmount = assetAmount;
		this.exchangeRateAUD = exchangeRateAUD;
		this.balanceDate = balanceDate;
		this.balanceType = balanceType;
	}
	
	
	public enum BalanceType{
		
		/**
		 *  Describes the asset balance
		 *  of a single transaction
		 * 
		 */
		Transaction,
		
		/**
		 * 	Describes the asset balance
		 *  of multiple transaction at a
		 *  point in time.
		 * 
		 */
		Sum
		
	}
	
	
	
	public String getAssetName() {
		return assetName;
	}
	public Double getAssetAmount() {
		return assetAmount;
	}
	
	public Double getExchangeRateAUD() {
		return exchangeRateAUD;
	}

	public LocalDateTime getBalanceDate() {
		return balanceDate;
	}
	
}
