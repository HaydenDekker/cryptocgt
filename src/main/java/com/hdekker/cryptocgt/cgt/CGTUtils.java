package com.hdekker.cryptocgt.cgt;

import static com.hdekker.cryptocgt.balance.BalanceAssesment.sumCoinOrderBalance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.data.CGTEvent;
import com.hdekker.cryptocgt.data.AssetBalance;
import com.hdekker.cryptocgt.data.AssetBalance.BalanceType;
import com.hdekker.cryptocgt.reports.Utils;

@Component
public class CGTUtils {

	public static 
	Function<Function<AssetBalance, String>,
		Function<List<AssetBalance>, Map<String, List<AssetBalance>>>> convertToHashMap(){
		return (keyMapper) ->  (list) -> {
			
			// just want to accumulate like cob's
			BinaryOperator<List<AssetBalance>> merger = (a, b) -> {
				
				List<AssetBalance> nl = new ArrayList<>(a);
				nl.addAll(b);
				return nl;
				
			};
			
			return list.stream()
				.collect(Collectors.toMap(keyMapper, cob -> Arrays.asList(cob), merger));
			
		};
	}
	
	public static BiFunction<List<AssetBalance>, AssetBalance, List<AssetBalance>> addBalanceToList(){
		return (list, bal)-> {
			List<AssetBalance> l = new ArrayList<>(list);
			l.add(bal);
			return l;
		};
	}

	
	public Map<String, List<AssetBalance>> mapAssetBalancesByAssetName(List<AssetBalance> balances){
				
				Map<String, List<AssetBalance>> map = convertToHashMap()
						.apply(AssetBalance::getAssetName)
						.apply(balances);
				
				map.keySet()
					.forEach(
						key->map.get(key)
								.sort((cob1, cob2) -> 
									cob1.getBalanceDate()
									.compareTo(cob2.getBalanceDate())
								)
								
							);
				
				return map;

		}
		

	
	// TODO only used in reporting of final CGT per year.
	public static Function<List<CGTEvent>, Double> cgtSummer(){
		return (list) -> list.stream()
								.map(e->e.getCgt())
								.reduce((a,n)-> a+n).orElse(0.0);
	}
	
	// TODO only used in reporting of final CGT per year.
	public static Function<List<CGTEvent>, List<CGTEvent>> dateFilter(LocalDateTime after, LocalDateTime before){
	
		return (list) -> list.stream().filter(l->{
			
			return l.getDisposedDate().compareTo(after)>0 && l.getDisposedDate().compareTo(before)<0;
		}).collect(Collectors.toList());
			
	}
	
}
