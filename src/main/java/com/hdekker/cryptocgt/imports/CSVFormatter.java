package com.hdekker.cryptocgt.imports;

import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Component;

@Component
public class CSVFormatter {

	public CSVFormat getFormatter(Class<? extends Enum<?>> e){
		
		CSVFormat format = CSVFormat.Builder
				.create()
				.setSkipHeaderRecord(true)
				.setHeader(e)
				.build();
		
		return format;
		
	}
	
}
