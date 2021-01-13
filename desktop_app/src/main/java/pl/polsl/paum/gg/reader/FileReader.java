package pl.polsl.paum.gg.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.opencsv.CSVReader;

public class FileReader {
	
	public List<String[]> readAll(Reader reader) throws IOException{
		try(CSVReader csvReader = new CSVReader(reader)){
			List<String[]> list = csvReader.readAll();
			return list;
		}
	}
}
