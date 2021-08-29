package com.hdekker.cryptocgt.imports;

import static com.hdekker.cryptocgt.imports.CSVUtils.configureColumn;
import static com.hdekker.cryptocgt.imports.CSVUtils.getColumnHeadings;
import static com.hdekker.cryptocgt.imports.CSVUtils.getObjectCreator;
import static com.hdekker.cryptocgt.imports.CSVUtils.openDocumentReader;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;

import com.hdekker.cryptocgt.UserConfig;
import com.hdekker.cryptocgt.data.Order;
import com.hdekker.cryptocgt.imports.CSVUtils.Converters;

import reactor.util.function.Tuple2;


public interface OrdersConfig {
	

		public static final String TransactionDate = "Transaction Date";
		public static final String  Type = "Type";
		public static final String Market = "Market";
		public static final String Amount = "Amount";
		public static final String Rateincfee = "Rate inc. fee";
		public static final String Rateexfee = "Rate ex. fee";
		public static final String Fee = "Fee";
		public static final String FeeAUDincGST = "Fee AUD (inc GST)";
		public static final String GSTAUD = "GST AUD";
		public static final String TotalAUD = "Total AUD";
		public static final String TotalincGST = "Total (inc GST)";
	
	public static Function<Tuple2<BufferedReader, List<String>>, List<Order>> getOrders(){
		
		return (tupe2) -> {
			
			HashMap<String, BiFunction<Order, String,  Order>> objectSetters = new HashMap<>();
			objectSetters.put(Amount, configureColumn(Converters.doubleConverter, Order::setAmount, Order.class));
			objectSetters.put(Fee, configureColumn(Converters.stringConverter, Order::setFee, Order.class));
			objectSetters.put(FeeAUDincGST, configureColumn(Converters.doubleConverter, Order::setFeeIncGST, Order.class));
			objectSetters.put(GSTAUD, configureColumn(Converters.doubleConverter, Order::setGst, Order.class));
			objectSetters.put(Market, configureColumn(Converters.stringConverter, Order::setMarket, Order.class));
			objectSetters.put(Rateexfee, configureColumn(Converters.doubleConverter, Order::setRateExFee, Order.class));
			objectSetters.put(Rateincfee, configureColumn(Converters.doubleConverter, Order::setRateIncFee, Order.class));
			objectSetters.put(TotalAUD, configureColumn(Converters.doubleConverter, Order::setTotalAUD, Order.class));
			objectSetters.put(TotalincGST, configureColumn(Converters.stringConverter, Order::setTotalIncGST, Order.class));
			objectSetters.put(TransactionDate, configureColumn(Converters.dateTimeConverter, Order::setTransactionDate, Order.class));
			objectSetters.put(Type, configureColumn(Converters.transactionTypeConv, Order::setTransactionType, Order.class));
		
			List<Order> allOrders = getObjectCreator(objectSetters, Order.class)
												.apply(tupe2.getT1(), tupe2.getT2());
		
			return allOrders;
		};
		
	}
}
