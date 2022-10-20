package com.hdekker.cryptocgt.imports;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.balance.AssetBalanceListCombiner;
import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.data.transaction.SendRecieves;
import com.hdekker.cryptocgt.imports.coinspot.CoinspotOrderAssetBalanceConverter;
import com.hdekker.cryptocgt.imports.coinspot.CoinspotSendReceiveAssetBalanceConverter;
import com.hdekker.cryptocgt.imports.coinspot.OrdersCSVExtractor;
import com.hdekker.cryptocgt.imports.coinspot.SendRecieveCSVExtractor;

@Component
public class CoinspotImporter {
	
	@Autowired
	SendRecieveCSVExtractor sendRecieveExtractor;
	
	@Autowired
	OrdersCSVExtractor ordersExtractor;
	
	@Autowired
	CoinspotOrderAssetBalanceConverter orderConverter;
	
	@Autowired
	CoinspotSendReceiveAssetBalanceConverter sendrecieveConverter;
	
	@Autowired
	AssetBalanceListCombiner combiner;

	public List<AssetBalance> importFromCoinSpot(
			Reader coinSpotOrdersReader, 
			Reader coinSpotSendReceivesReader) throws Exception{

		List<Order> ord = ordersExtractor.getOrders(coinSpotOrdersReader);
		List<SendRecieves> srs = sendRecieveExtractor.getSendRecieves(coinSpotSendReceivesReader);
		
		List<AssetBalance> orders = orderConverter.getAssetBalancesForOrders(ord);
		List<AssetBalance> sendReceives = srs
												.stream().map(sr-> sendrecieveConverter.sendRecieveToCoinOrderBalance(sr))
												.collect(Collectors.toList());
		return combiner.combine(orders, sendReceives);

	}
	
}
