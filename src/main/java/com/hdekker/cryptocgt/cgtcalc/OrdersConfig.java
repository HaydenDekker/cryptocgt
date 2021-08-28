package com.hdekker.cryptocgt.cgtcalc;

import static com.hdekker.cryptocgt.interfaces.CSVUtils.configureColumn;
import static com.hdekker.cryptocgt.interfaces.CSVUtils.getColumnHeadings;
import static com.hdekker.cryptocgt.interfaces.CSVUtils.getObjectCreator;
import static com.hdekker.cryptocgt.interfaces.CSVUtils.openDocumentReader;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import org.springframework.context.annotation.Configuration;

import com.hdekker.cryptocgt.data.Order;
import com.hdekker.cryptocgt.interfaces.CSVUtils.Converters;

@Configuration
public class OrdersConfig {

	enum ColumnNames{
		
		TransactionDate("Transaction Date"),
		Type("Type"),
		Market("Market"),
		Amount("Amount"),
		Rateincfee("Rate inc. fee"),
		Rateexfee("Rate ex. fee"),
		Fee("Fee"),
		FeeAUDincGST("Fee AUD (inc GST)"),
		GSTAUD("GST AUD"),
		TotalAUD("Total AUD"),
		TotalincGST("Total (inc GST)");
	
		String name;
				
		ColumnNames(String name){
			this.name = name;
		}
		
	}
	
	public List<Order> getOrders(){
		
		//"/Users/HDekker/Documents/2020/September/Tax/Crypto/orderhistory.csv"
		// C:\Users\HDekker\Documents\2021\August 2021\Tax
		String pathOrderHistoryCSV = "/Users/HDekker/Documents/2021/August 2021/Tax/orderhistory.csv";
		HashMap<String, BiFunction<Order, String,  Order>> objectSetters = new HashMap<>();
		objectSetters.put(ColumnNames.Amount.name, configureColumn(Converters.doubleConverter, Order::setAmount, Order.class));
		objectSetters.put(ColumnNames.Fee.name, configureColumn(Converters.stringConverter, Order::setFee, Order.class));
		objectSetters.put(ColumnNames.FeeAUDincGST.name, configureColumn(Converters.doubleConverter, Order::setFeeIncGST, Order.class));
		objectSetters.put(ColumnNames.GSTAUD.name, configureColumn(Converters.doubleConverter, Order::setGst, Order.class));
		objectSetters.put(ColumnNames.Market.name, configureColumn(Converters.stringConverter, Order::setMarket, Order.class));
		objectSetters.put(ColumnNames.Rateexfee.name, configureColumn(Converters.doubleConverter, Order::setRateExFee, Order.class));
		objectSetters.put(ColumnNames.Rateincfee.name, configureColumn(Converters.doubleConverter, Order::setRateIncFee, Order.class));
		objectSetters.put(ColumnNames.TotalAUD.name, configureColumn(Converters.doubleConverter, Order::setTotalAUD, Order.class));
		objectSetters.put(ColumnNames.TotalincGST.name, configureColumn(Converters.stringConverter, Order::setTotalIncGST, Order.class));
		objectSetters.put(ColumnNames.TransactionDate.name, configureColumn(Converters.dateTimeConverter, Order::setTransactionDate, Order.class));
		objectSetters.put(ColumnNames.Type.name, configureColumn(Converters.transactionTypeConv, Order::setTransactionType, Order.class));
		
		BufferedReader br = openDocumentReader().apply(pathOrderHistoryCSV).orElseThrow();

		List<Order> allOrders = getObjectCreator(objectSetters, Order.class)
											.apply(br, getColumnHeadings().apply(br));
	
		return allOrders;
		
	}
}
