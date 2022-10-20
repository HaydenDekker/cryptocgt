package com.hdekker.cryptocgt.imports.coinspot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.springframework.stereotype.Component;

@Component
public class CoinspotDateTimeConverter {
	
	final static String coinSpotDateTimeFormat = "d[d]/M[M]/yyyy [ ]H[H]:mm[:ss]";
			//"d[d]/M[M]/yyyy [ ]h[h]:mm[:ss][ a]";
	
	private DateTimeFormatter formatter;
	
	public CoinspotDateTimeConverter() {
		formatter = new DateTimeFormatterBuilder()
					.parseCaseInsensitive()
					.appendPattern(coinSpotDateTimeFormat)
					.toFormatter();
	}

	public LocalDateTime convert(String dateTime) {
		
			return LocalDateTime.parse(dateTime, formatter);
		
	}
	
}
