package com.hdekker.cryptocgt.interfaces;

import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public interface Utils {
	
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
