package com.hdekker.cryptocgt.imports.coinspot;

import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.data.AssetBalance.BalanceType;
import com.hdekker.cryptocgt.data.transaction.SendRecieves;

@Component
public class CoinspotSendReceiveAssetBalanceConverter {

	public AssetBalance sendRecieveToCoinOrderBalance(SendRecieves sr){

			AssetBalance cob = new AssetBalance(
					sr.getCoin(),
					sr.getAmount(),
					sr.getExchangeRateAUD(),
					sr.getTransactionDate(),
					BalanceType.Transaction
					);

			return cob;
	}
	
}
