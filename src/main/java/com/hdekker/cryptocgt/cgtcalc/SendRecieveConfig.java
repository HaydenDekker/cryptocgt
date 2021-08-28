package com.hdekker.cryptocgt.cgtcalc;

import static com.hdekker.cryptocgt.interfaces.CSVUtils.*;
import static com.hdekker.cryptocgt.interfaces.CoinPriceCheck.getCoinPriceAtDate;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.hdekker.cryptocgt.data.SendRecieves;
import com.hdekker.cryptocgt.interfaces.CSVUtils.Converters;

import static com.hdekker.cryptocgt.interfaces.CoinPriceCheck.*;

@Configuration
public class SendRecieveConfig {

	// /Users/HDekker/Documents/2020/September/Tax/Crypto/sendsreceives.csv"
	// C:\Users\HDekker\Documents\2021\August 2021\Tax
	String path = "/Users/HDekker/Documents/2021/August 2021/Tax/sendsreceives.csv";
	
	public List<SendRecieves> getSendRecieves(){
		
		BufferedReader br = openDocumentReader().apply(path).orElseThrow();
		
		HashMap<String, BiFunction<SendRecieves, String, SendRecieves>> map = new HashMap<>();
		map.put("Transaction Date", configureColumn(Converters.dateTimeConverter, SendRecieves::setTransactionDate, SendRecieves.class));
		map.put("Type", configureColumn(Converters.transactionTypeConv, SendRecieves::setType, SendRecieves.class));
		map.put("Coin", configureColumn(Converters.stringConverter, SendRecieves::setCoin, SendRecieves.class));
		map.put("Amount",configureColumn(Converters.doubleConverter, SendRecieves::setAmount, SendRecieves.class));
		
		List<String> headings = getColumnHeadings().apply(br);
		
		List<SendRecieves> srs = getObjectCreator(map, SendRecieves.class).apply(br, headings);
		
		List<SendRecieves> sendsRecieves = srs.stream()
				.map(sr-> setSendRecieveExchangeAUDRate()
								.apply(sr)
				).collect(Collectors.toList());
		
		return sendsRecieves;
		
	}
			
	
}


