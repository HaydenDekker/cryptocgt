package com.hdekker.cryptocgt.interfaces;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;
import java.util.function.Function;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.hdekker.cryptocgt.data.SendRecieves;

import reactor.core.publisher.Mono;

/**
 * Coinspot did provide values at the send, recieve dates.
 * 
 * 28-08-2021 From now on the price needs to be looked up for sends and recieves.
 * Coinspot dosen't provide a tool to capture the asset value at a particular date. Stupid.
 * 
 * @author HDekker
 *
 */
@Deprecated
public interface CoinPriceCheck {
	
		public static Function<LocalDateTime, Mono<ResponseEntity<String>>> getCoinPriceAtDate(String coinName){
		
			WebClient client = WebClient.create("https://api.coingecko.com/api/v3/");
				
			// Coinspot coin symbol to coin gecko id mapping	
			Map<String, String> coins = Map.of("BTC", "bitcoin", "LTC", "litecoin", "XRP", "ripple");
			String coin = coins.get(coinName);
			String url = "coins/" + coin +"/history";
			
		
			return (date)->{
				
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
															.appendPattern("dd-MM-yyyy")
															.toFormatter();
				String dateAttr = date.format(formatter);
				
				ResponseSpec resp = client.get()
						.uri(uribuilder->
							
							uribuilder.path(url)
							.queryParam("date", dateAttr)
							.build()
							
						)
						.retrieve();
						
				
				return resp.toEntity(String.class);
				
		};
	}
	
	public static Function<String, Double> extractMarketAmount(){
	
		return (json) -> {
		try {
			JSONObject obj = new JSONObject(json);
			JSONObject mkdata = obj.getJSONObject("market_data");
			JSONObject currentPrice = mkdata.getJSONObject("current_price");
			return currentPrice.getDouble("aud");

		} catch (JSONException e) {
			e.printStackTrace();
		}
			return 0.00;
		
		};
	}
	
	public static Function<SendRecieves, SendRecieves> setSendRecieveExchangeAUDRate(){
		return (sr) -> {
					String marketInfo = getCoinPriceAtDate(sr.getCoin())
							.apply(sr.getTransactionDate())
							.block().getBody();
					
					Double amt = extractMarketAmount().apply(marketInfo);
					
					SendRecieves cpy = Utils.deepCopier(SendRecieves.class).apply(sr);
					cpy.setExchangeRateAUD(amt);
					return cpy;
		};
	}

}
