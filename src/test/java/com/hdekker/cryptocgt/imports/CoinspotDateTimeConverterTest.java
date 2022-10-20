package com.hdekker.cryptocgt.imports;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.imports.coinspot.CoinspotDateTimeConverter;

@SpringBootTest
public class CoinspotDateTimeConverterTest {

	@Autowired
	CoinspotDateTimeConverter converter;
	
	@Test
	public void convertsDateTime() {
		//14/10/2022  4:26:00 PM
		String timeString = "24/8/2021  22:43";
				//"24/8/2021  10:43:00 PM"; 
		LocalDateTime conv = converter.convert(timeString);
		assertThat(conv, equalTo(LocalDateTime.of(2021, 8, 24, 22, 43, 0, 0)));
		
	}
	
}
