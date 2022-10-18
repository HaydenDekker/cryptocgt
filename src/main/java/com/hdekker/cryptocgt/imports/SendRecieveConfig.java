package com.hdekker.cryptocgt.imports;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.hdekker.cryptocgt.data.transaction.SendRecieves;
import com.hdekker.cryptocgt.data.transaction.TransactionType;

@Configuration
public class SendRecieveConfig {
	
	Logger log = LoggerFactory.getLogger(SendRecieveConfig.class);

	public enum SendReceiveColumns{
		TRANSACTION_DATE,
		TYPE,
		COIN,
		AMOUNT,
		EX_AUD_RATE
	}
	
	@Autowired
	CSVFormatter formatter;
	
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
					SendRecieves srs = new SendRecieves(
							rec.get(SendReceiveColumns.COIN), 
							Double.valueOf(rec.get(SendReceiveColumns.AMOUNT)), 
							CSVUtils.Converters.dateTimeConverter
								.apply(rec.get(SendReceiveColumns.TRANSACTION_DATE)), 
							TransactionType.valueOf(rec.get(SendReceiveColumns.TYPE)), 
							Double.valueOf(rec.get(SendReceiveColumns.EX_AUD_RATE)));
					return srs;
				})
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new Exception("Couldn't import Send Receives", e);
		}
				
	}
			
	
}


