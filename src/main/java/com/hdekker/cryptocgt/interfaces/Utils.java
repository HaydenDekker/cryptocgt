package com.hdekker.cryptocgt.interfaces;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdekker.cryptocgt.data.AccountBalanceSnapshot;
import com.hdekker.cryptocgt.data.CoinBalance;
import com.hdekker.cryptocgt.data.transaction.Order;


public interface Utils {

	static <T> Function<T, T> deepCopier(Class<T> clazz){
		
		return (obj) -> {
		// deep copy
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		T copy = null;
		try {
			copy = om.treeToValue(om.valueToTree(obj), clazz);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return copy;
		};
	}
	
	public static Function<Order, Order> orderDeepCopy(){
		return deepCopier(Order.class);
	}
	
	
	public static Function<CoinBalance, CoinBalance> coinBalanceDeepCopy(){
		return deepCopier(CoinBalance.class);
	}
	
	
	public static Function<AccountBalanceSnapshot, AccountBalanceSnapshot> accBalSnapDeepCpy(){
		return deepCopier(AccountBalanceSnapshot.class);
	}
	
	public static <T> Function<Object, String> toJson(Class<T> clazz){
		
		return (obj) -> {
			
			ObjectMapper om = new ObjectMapper();
			om.registerModule(new JavaTimeModule());
			
			try {
				return om.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "error";
			
		};
		
	}
	
}
