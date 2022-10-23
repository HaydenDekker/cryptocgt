package com.hdekker.cryptocgt.reports;

import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.AppConfig;

@Component
public class AssetToTaxYearMapper {

	@Autowired
	AppConfig config;
	
	Year getYear(LocalDateTime dateTime) {
		// Needed Calendar as Spring Config wanted to import that
		// TODO try use MonthDay with spring config.
		Calendar endOfTaxYear = config.getTaxYearEnd();
		
		MonthDay eoy = MonthDay.of(
					endOfTaxYear.get(Calendar.MONTH), 
					endOfTaxYear.get(Calendar.DAY_OF_MONTH)
					);
		MonthDay dt = MonthDay.of(
				dateTime.getMonthValue(), 
				dateTime.getDayOfMonth());
		
		return (eoy.compareTo(dt)<0) ? 
						Year.of(dateTime.getYear() + 1):
							Year.of(dateTime.getYear());
		
	}
	
	public <T> Map<Year, List<T>> mapByYear(List<T> events, Function<T, LocalDateTime> eventDateTimeProvider){
		
		Function<T, Year> keyMap = (t) -> getYear(eventDateTimeProvider.apply(t));
		Function<T, List<T>> valueMap = (t) -> List.of(t);
		BinaryOperator<List<T>> merger = (lto, ltn) -> {
			List<T> l = new ArrayList<>();
			l.addAll(lto);
			l.addAll(ltn);
			return l;
		};
		
		return events.stream()
					.collect(Collectors.toMap(
							keyMap,
							valueMap,
							merger));

	}
	
}
