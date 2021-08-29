package com.hdekker.cryptocgt.imports;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import com.hdekker.cryptocgt.imports.CSVUtils.Converters;

import reactor.util.function.Tuple2;

import static com.hdekker.cryptocgt.imports.CSVUtils.*;

public interface SendRecieveConfig {

	// TODO app sepcifc, they need to ensure exchange rate was captured for the
	// day and time the asset was transfered in of out of the account.
	public static final String EX_AUD_RATE = "Ex AUD rate";
	public static final String AMOUNT = "Amount";
	public static final String COIN = "Coin";
	public static final String TYPE = "Type";
	public static final String TRANSACTION_DATE = "Transaction Date";
	
	List<String> requiredColumns = Arrays.asList(TRANSACTION_DATE, TYPE, COIN, AMOUNT, EX_AUD_RATE);
	
	Predicate<List<String>> hasRequiredColumns = (in) -> in.containsAll(requiredColumns);
	
	/**
	 * Requires columns should be present, else no gaurantees
	 * 
	 * Extracts CSV's
	 * 
	 * @return
	 */
	public static Function<Tuple2<BufferedReader, List<String>>, List<SendRecieves>> getSendRecieves(){
		
		return (csvBrAndHeadings) -> {
	
			HashMap<String, BiFunction<SendRecieves, String, SendRecieves>> map = new HashMap<>();
			map.put(TRANSACTION_DATE, configureColumn(Converters.dateTimeConverter, SendRecieves::setTransactionDate, SendRecieves.class));
			map.put(TYPE, configureColumn(Converters.transactionTypeConv, SendRecieves::setType, SendRecieves.class));
			map.put(COIN, configureColumn(Converters.stringConverter, SendRecieves::setCoin, SendRecieves.class));
			map.put(AMOUNT,configureColumn(Converters.doubleConverter, SendRecieves::setAmount, SendRecieves.class));
			map.put(EX_AUD_RATE, configureColumn(Converters.doubleConverter, SendRecieves::setExchangeRateAUD, SendRecieves.class));
			List<SendRecieves> srs = getObjectCreator(map, SendRecieves.class).apply(csvBrAndHeadings.getT1(), csvBrAndHeadings.getT2());	
			return srs;
		}; 
		
	}
			
	
}


