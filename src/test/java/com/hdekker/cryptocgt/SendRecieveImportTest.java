package com.hdekker.cryptocgt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.cryptocgt.cgtcalc.SendRecieveConfig;
import com.hdekker.cryptocgt.data.SendRecieves;
import com.hdekker.cryptocgt.interfaces.Utils;
import com.hdekker.cryptocgt.interfaces.CSVUtils.Converters;

import static com.hdekker.cryptocgt.interfaces.BalanceSnapshotUtils.*;
import static com.hdekker.cryptocgt.interfaces.CoinPriceCheck.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
public class SendRecieveImportTest {

	@Autowired
	SendRecieveConfig config;
	
	@Test
	public void importDoc() {
		
		List<SendRecieves> ojbs = config.getSendRecieves();
		assertTrue(ojbs.size()==12);
		
	}
	
	@Test
	public void coinPriceSet() {
		
		List<SendRecieves> sendsRecieves = config.getSendRecieves().stream()
							.map(sr-> setSendRecieveExchangeAUDRate()
											.apply(sr)
							).collect(Collectors.toList());
		
	}
	
	@Test
	public void sendRecieveToCoinOrderBalance() {
		
		
	}
}
