package com.hdekker.cryptocgt.imports.coinspot;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.hdekker.cryptocgt.data.transaction.SendRecieves;
import com.hdekker.cryptocgt.data.transaction.TransactionType;
import com.hdekker.cryptocgt.imports.CSVFormatter;

@Configuration
public class SendRecieveCSVExtractor {
	
	Logger log = LoggerFactory.getLogger(SendRecieveCSVExtractor.class);

	public enum SendReceiveColumns{
		TRANSACTION_DATE,
		TYPE,
		COIN,
		STATUS,
		FEE,
		AMOUNT,
		ADDRESS,
		TXID,
		EX_AUD_RATE
	}
	
	@Autowired
	CSVFormatter formatter;
	
	@Autowired
	CoinspotDateTimeConverter dateTimeConverter;
	
	/**
	 * Requires columns should be present, else no gaurantees
	 * 
	 * Extracts CSV's
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<SendRecieves> getSendRecieves(Reader reader) throws Exception{
		
		try {
			return formatter.getFormatter(SendReceiveColumns.class)
				.parse(reader)
				.stream()
				.map(rec->{
					
					Double fee = 0.0;
					
					try {
						fee = Double.valueOf(rec.get(SendReceiveColumns.EX_AUD_RATE));
					}catch(Exception e){
						fee = 0.0;
					}
					
					SendRecieves srs = new SendRecieves(
							rec.get(SendReceiveColumns.COIN), 
							Double.valueOf(rec.get(SendReceiveColumns.AMOUNT)), 
							dateTimeConverter.convert(rec.get(SendReceiveColumns.TRANSACTION_DATE)), 
							TransactionType.valueOf(rec.get(SendReceiveColumns.TYPE)),
							fee);
					return srs;
				})
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new Exception("Couldn't import Send Receives", e);
		}
				
	}
			
	
}


