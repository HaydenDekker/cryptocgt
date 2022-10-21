package com.hdekker.cryptocgt.cgt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 
 * An assetBalance may or may not cause CGT events,
 * Initial purchasing doesn't cause CGT events only Disposals.
 * 
 * All Buys are initial purchases.
 * The initial Receives can be considered buys too.
 * 
 * CGT Events relate to just a single asset.
 * No need to consider other assets in the calculation.
 * 
 * 
 */
@SpringBootTest
public class CGTCalculatorTest {

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

		BiFunction<LocalDateTime, List<LocalDateTime>, LocalDateTime> fn = 
				CGTCalculator.findTheMostRecentPurchase((ldt)->ldt);
		LocalDateTime cob = fn.apply(cob1, Arrays.asList(cob3, cob2, cob4));
		assertTrue(cob.equals(cob2));
		
	}
	
}
