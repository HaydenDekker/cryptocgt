package com.hdekker.cryptocgt.imports;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.data.TransactionType;
import com.hdekker.cryptocgt.data.transaction.Order;

@Component
public class OrdersCSVExtractor {
	
	enum OrderColumns{
	
		TransactionDate,
		Type,
		Market,
		Amount,
		Rateincfee,
		Rateexfee,
		Fee,
		FeeAUDincGST,
		GSTAUD,
		TotalAUD,
		TotalincGST
	}
	
	@Autowired
	CSVFormatter formatter;
	
	public List<Order> getOrders(Reader isr) throws Exception{
			
			try {
				return formatter.getFormatter(OrderColumns.class)
					.parse(isr)
					.stream()
					.map(rec->{
						
						Order o = new Order(
								CSVUtils.Converters
									.dateTimeConverter
									.apply(rec.get(OrderColumns.TransactionDate)),
								TransactionType.valueOf(rec.get(OrderColumns.Type)),
								rec.get(OrderColumns.Market),
								Double.valueOf(rec.get(OrderColumns.Amount)),
								Double.valueOf(rec.get(OrderColumns.Rateincfee)), 
								Double.valueOf(rec.get(OrderColumns.Rateexfee)), 
								rec.get(OrderColumns.Fee), 
								Double.valueOf(rec.get(OrderColumns.FeeAUDincGST)), 
								Double.valueOf(rec.get(OrderColumns.GSTAUD)), 
								Double.valueOf(rec.get(OrderColumns.TotalAUD)), 
								rec.get(OrderColumns.TotalincGST));
						
						return o;
						
					})
					.collect(Collectors.toList());
			} catch (IOException e) {
				throw new Exception("Couldn't import orders.", e);
			}
					
			
		

		//parser = format.parse(null)
		
//			HashMap<String, BiFunction<Order, String,  Order>> objectSetters = new HashMap<>();
//			objectSetters.put(Amount, configureColumn(Converters.doubleConverter, Order::setAmount, Order.class));
//			objectSetters.put(Fee, configureColumn(Converters.stringConverter, Order::setFee, Order.class));
//			objectSetters.put(FeeAUDincGST, configureColumn(Converters.doubleConverter, Order::setFeeIncGST, Order.class));
//			objectSetters.put(GSTAUD, configureColumn(Converters.doubleConverter, Order::setGst, Order.class));
//			objectSetters.put(Market, configureColumn(Converters.stringConverter, Order::setMarket, Order.class));
//			objectSetters.put(Rateexfee, configureColumn(Converters.doubleConverter, Order::setRateExFee, Order.class));
//			objectSetters.put(Rateincfee, configureColumn(Converters.doubleConverter, Order::setRateIncFee, Order.class));
//			objectSetters.put(TotalAUD, configureColumn(Converters.doubleConverter, Order::setTotalAUD, Order.class));
//			objectSetters.put(TotalincGST, configureColumn(Converters.stringConverter, Order::setTotalIncGST, Order.class));
//			objectSetters.put(TransactionDate, configureColumn(Converters.dateTimeConverter, Order::setTransactionDate, Order.class));
//			objectSetters.put(Type, configureColumn(Converters.transactionTypeConv, Order::setTransactionType, Order.class));
//		
//			List<Order> allOrders = getObjectCreator(objectSetters, Order.class)
//												.apply(tupe2.getT1(), tupe2.getT2());
//		
//			return allOrders;
		
	}
}
