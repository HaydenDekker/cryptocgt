package com.hdekker.cryptocgt.cgt.search;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class MoreThan12MonthDateSearcher implements DateSearcher{
	
	public <T> BiFunction<T, List<T>, Optional<T>> search(Function<T, LocalDateTime> dateProvider){
		return (fromDate, list) -> {
			
			List<T> sorted = list.stream()
								.sorted(
									(cob1, cob2)-> dateProvider.apply(cob2).compareTo(dateProvider.apply(cob1)))
								.collect(Collectors.toList());
			
			return sorted.stream()
					.filter(cob1->dateProvider.apply(fromDate)
							.minusYears(1)
							.compareTo(dateProvider.apply(cob1))>0)
					.findFirst();
		};
	}
}

