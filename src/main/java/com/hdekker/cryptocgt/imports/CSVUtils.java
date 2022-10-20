package com.hdekker.cryptocgt.imports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdekker.cryptocgt.data.transaction.TransactionType;


public class CSVUtils {

	/**
	 * Opens any document as optional
	 * 
	 * @return
	 */
	public static Function<String, Optional<BufferedReader>> openDocumentReader(){
		
		return (path)->{
			
			FileReader document = null;
			
			try {
				document = new FileReader(new File(path));
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
			
			if(document == null) return Optional.empty();
			
			return Optional.of(new BufferedReader(document));
			
		};
		
	}

}
