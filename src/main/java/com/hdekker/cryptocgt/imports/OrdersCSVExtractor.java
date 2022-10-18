package com.hdekker.cryptocgt.imports;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.data.transaction.Order;
import com.hdekker.cryptocgt.data.transaction.TransactionType;

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
								Double.valueOf(rec.get(OrderColumns.TotalAUD)), 
								rec.get(OrderColumns.TotalincGST));
						
						return o;
						
					})
					.collect(Collectors.toList());
			} catch (IOException e) {
				throw new Exception("Couldn't import orders.", e);
			}
					
	}
}
