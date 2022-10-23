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
public class MostRecentDateSearcherTest {

	@Autowired
	MostRecentDateSearcher searcher;
	
	/**
	 * 
	 */
	@Test
	public void uMostRecentIsFound() {
		
		LocalDateTime cob1 = 
				LocalDateTime.now();
		
		LocalDateTime cob2 = 
				LocalDateTime.now().minusDays(1);

		LocalDateTime cob3 = 
				LocalDateTime.now().minusDays(2);
		
		LocalDateTime cob4 =
				LocalDateTime.now().plusDays(2);

		BiFunction<LocalDateTime, List<LocalDateTime>, Optional<LocalDateTime>> fn = 
				searcher.search((ldt)->ldt);
		LocalDateTime cob = fn.apply(cob1, Arrays.asList(cob3, cob2, cob4))
				.get();
		assertTrue(cob.equals(cob2));
		
	}
	
}
