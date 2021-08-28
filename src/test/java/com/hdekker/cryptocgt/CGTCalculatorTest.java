package com.hdekker.cryptocgt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CGTCalculatorTest {

	@Test
	public void calcCGT() {
		new CGTCalculator();
	}
	
}
