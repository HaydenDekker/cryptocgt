package com.hdekker.cryptocgt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.UserConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class UserConfigTest {

	@Autowired
	UserConfig userConfig;
	
	/**
	 *  Should fail for if you change the app config
	 *  put your's here to ensure spring is getting
	 *  the right app config from you file.
	 *  
	 *  Development only
	 *  
	 *  Obviously you can change it indenpendently at
	 *  runtime.
	 * 
	 */
	@Test
	public void canGetConfiguredValuesInSpring() {
		
		assertThat(userConfig.getBuysSellsCSV(), equalTo("/Users/HDekker/Documents/2021/August 2021/Tax/orderhistory.csv"));
		
	}
	
}
