package com.hdekker.cryptocgt.reports;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.data.CGTEvent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class AssetToTaxYearMapperTest {

	@Autowired
	AssetToTaxYearMapper mapper;
	
	@Test
	public void findsTaxYear() {
		
		Year year = mapper.getYear(LocalDateTime.of(2021, 5, 5, 0, 0));
		assertThat(year, equalTo(Year.of(2021)));
		
		Year year2 = mapper.getYear(LocalDateTime.of(2021, 7, 5, 0, 0));
		assertThat(year2, equalTo(Year.of(2022)));
		
	}
	
	public List<CGTEvent> events(){
		return List.of(
				new CGTEvent(
					LocalDateTime.now(), 
					null, 
					null, 
					null)
				);
	}
	
	@Test
	public void mapsTaxEventsByTaxYear(){
		
		Map<Year, List<CGTEvent>> events = mapper.mapByYear(events(), CGTEvent::getDisposedDate);
		
		assertThat(events.size(), equalTo(1));
		
		Year y = mapper.getYear(LocalDateTime.now());
		assertThat(events.get(y), notNullValue());
		
	}
	
}
