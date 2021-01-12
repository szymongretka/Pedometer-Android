package pl.polsl.paum.gg.controller;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;

public class CsvController {
	private final CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
	
}
