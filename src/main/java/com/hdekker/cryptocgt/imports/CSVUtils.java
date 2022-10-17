package com.hdekker.cryptocgt.imports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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
import com.hdekker.cryptocgt.data.TransactionType;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class CSVUtils {
	
	// TODO application specific
	final static String coinSpotDateTimeFormat = "d/M/y k:m";
	
	public static class Converters {
		
		// Converters
		public static Function<String, Double> doubleConverter = (string) -> Double.valueOf(string);
		public static Function<String, String> stringConverter = (string) -> string;
		
		public static Function<String, LocalDateTime> dateTimeConverter = (string) -> 
									{
										DateTimeFormatter formatter = new DateTimeFormatterBuilder().
																	appendPattern(coinSpotDateTimeFormat)
																	.toFormatter();
										return LocalDateTime.parse(string.replaceAll(" +", " "), formatter);
									};
									
		public static Function<String, TransactionType> transactionTypeConv = (string) -> {
			
			return Arrays.asList(TransactionType.values()).stream().filter(type-> string.equals(type.toString())).findFirst().get();
			
		};
	}

	/**
	 * Opens any document as optional
	 * 
	 * @return
	 */
	static Function<String, Optional<BufferedReader>> openDocumentReader(){
		
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
	
	/**
	 *  Make sure CVS values is ok 
	 *  Headings may have quotes "" so remove.
	 * 
	 */
	static Function<String, String> cleanCSVValues = (s) -> s.stripLeading().replaceAll("^\"|\"$", "");

	/**
	 * Must be called first after the Buffered reader is returned.
	 * 
	 * 
	 * @return
	 */
	public static Function<BufferedReader, List<String>> getColumnHeadings(){
		return (br) -> {
			
			List<String> columnOrder = null;
			
			try {
				columnOrder = Arrays.asList(br.readLine().split(","))
									.stream()
									.map(cleanCSVValues)
									.collect(Collectors.toList());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return columnOrder;
		};
	}

	
	/**
	 *  Check for required headings.
	 * 
	 */
	
	public BiPredicate<List<String>, List<String>> hasRequiredHeadings = (requiredHeadings, actualHeadings) -> {
		return actualHeadings.containsAll(requiredHeadings);
	};
	/**
	 *  App level function if reading from file.
	 * 
	 */
	public Function<String, Tuple2<BufferedReader, List<String>>> openDocumentAndGetHeadings
			= (doc) -> {
				BufferedReader reader = openDocumentReader().apply(doc).orElseThrow();
				return Tuples.of(reader, getColumnHeadings().apply(reader));
	};

	/**
	 * Use this to convert csv's to objects of you choosing
	 * 
	 * @param <T>
	 * @param setters
	 * @param clazz
	 * @return
	 */
	public static <T> BiFunction<BufferedReader, List<String>, List<T>> getObjectCreator(
											HashMap<String, BiFunction<T, String,  T>> setters, 
											Class<T> clazz){
			return (br, columnOrder) -> {
				
				return br.lines()
					.map(line-> splitByCommas().apply(line))
					.map(values -> 
					{
					T object = null;
					try {
					object = clazz.getConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
					
					for(int i = 0; i<columnOrder.size(); i++) {
					
						object = getAndSetOrLeave(
									object,
									setters.get(columnOrder.get(i)),
									values.get(i));
					
					}
					
						return object;
					
					})
					.collect(Collectors.toList());
				};

		}
	
		public static <T> T getAndSetOrLeave(T obj, BiFunction<T, String,  T> setter, String value) {
			
			return Optional.ofNullable(setter)
					.map(set->
					set.apply(obj, value)
				).orElse(obj);	
			
		}
	
		/**
		 * May want to implement this if you need another delimiter
		 * 
		 * @return
		 */
		public static Function<String, List<String>> splitByCommas(){
			return (string) -> 
				Arrays.asList(string.split(","));
	
		}
	
		/**
		 * Need to declare a map of column configuration to allow the 
		 * conversion of data and the setting of the object field
		 * 
		 * @param <T>
		 * @param conv - the converter of String to the required value
		 * @param setter - the type's method reference to set for the column
		 * @return
		 */
		public static <K, T> BiFunction<K, String,  K> configureColumn(Function<String, T> conv, BiConsumer<K, T> setter, Class<K> clazz) {
			
			return (obj, value) -> {
				
				// deep copy
				ObjectMapper om = new ObjectMapper();
				om.registerModule(new JavaTimeModule());
				K copy = null;
				try {
					copy = om.treeToValue(om.valueToTree(obj), clazz);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// set value
				setter.accept(copy, conv.apply(value));
				
				return copy;
				
			};
			
		}
		// this is for a later date.
//		
//		public static Function<List<String>, String> createCSVString(){
//			return (list) -> list.stream()
//								.map((str)-> "\"" + str + "\"")
//								.reduce((s, s2)-> s.concat("," + s2)).orElseThrow(); 
//			
//		}
//		
//		public static <T> Function<T, String> objectsToCSVString(){
//			return ()
//			
//		}
//		
//		public static <T> BiConsumer<List<Object>,String> writeCSV(HashMap<String, BiFunction<T, String,  T>> getters,
//				List<String> columnOrder,
//				Class<T> clazz){
//			
//			return (items, path) -> {
//				
//				FileWriter fw = null;
//				
//				try {
//					fw = new FileWriter(new File(path));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				BufferedWriter writer = new BufferedWriter(fw);
//				
//				try {
//					writer.write(createCSVString().apply(columnOrder));
//					writer.newLine();
//					
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				for(int i = 0; i<columnOrder.size(); i++) {
//					
//					
//					
//				}
//				
//			};
//			
//		}
}
