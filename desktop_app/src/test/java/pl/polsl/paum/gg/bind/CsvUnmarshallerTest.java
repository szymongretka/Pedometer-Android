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

import pl.polsl.paum.gg.bind.impl.CsvUnmarshallerImpl;
import pl.polsl.paum.gg.exception.ConversionException;

public class CsvUnmarshallerTest {

	CsvUnmarshallerImpl csvUnmarshaller;

	@BeforeEach
	void init() {
		csvUnmarshaller = new CsvUnmarshallerImpl();
	}

	@Test
	public void testUnmarshalCsvToPojo() {
		Path path = Paths.get("src", "test", "resources", "stepsWithDates.csv");
		try {
			csvUnmarshaller.unmarshalCsvToPojo(Files.newBufferedReader(path));
		} catch (IOException | ConversionException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			fail("Exception occured. Stack trace: " + sw.toString());
		}
	}

}
