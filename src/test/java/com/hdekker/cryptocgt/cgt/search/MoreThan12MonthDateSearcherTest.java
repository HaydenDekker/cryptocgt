package com.hdekker.cryptocgt.cgt.search;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MoreThan12MonthDateSearcherTest {
	
	@Autowired
	MoreThan12MonthDateSearcher searcher;

	@Test
	public void findsnAnyOlderThan12Months() {
		
		LocalDateTime cob1 = 
				LocalDateTime.now();
		
		LocalDateTime cob2 = 
				LocalDateTime.now().plusDays(380);

		LocalDateTime cob3 = 
				LocalDateTime.now().minusDays(58);
		
		LocalDateTime cob4 =
				LocalDateTime.now().minusDays(380);

		BiFunction<LocalDateTime, List<LocalDateTime>, Optional<LocalDateTime>> fn = 
				searcher.search((ldt)->ldt);
		LocalDateTime cob = fn.apply(cob1, Arrays.asList(cob3, cob2, cob4))
				.get();
		assertTrue(cob.equals(cob4));
		
	}
	
}
