package pl.polsl.paum.gg.bind;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CsvUnmarshallerTest {

	CsvUnmarshaller csvUnmarshaller;

	@BeforeEach
	void init() {
		csvUnmarshaller = new CsvUnmarshaller();
	}

	@Test
	public void testUnmarshalCsvToPojo() {
		Path path = Paths.get("src", "test", "resources", "stepsWithDates.csv");
		try {
			csvUnmarshaller.unmarshalCsvToPojo(Files.newBufferedReader(path));
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			fail("Exception occured. Stack trace: " + sw.toString());
		}
	}

}
