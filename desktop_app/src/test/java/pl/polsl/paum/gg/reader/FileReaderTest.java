package pl.polsl.paum.gg.reader;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileReaderTest {

	FileReader fileReader;

	@BeforeEach
	void init() {
		fileReader = new FileReader();
	}

	@Test
	void testFileRead() throws IOException {
		Path path = Paths.get("src", "test", "resources", "stepsWithDates.csv");
		StringBuilder fileContent = new StringBuilder();
		Files.readAllLines(path).forEach(s -> fileContent.append(s));
		System.out.println(fileContent.toString());
	}
	
	@Test
	void testReadAll() {
		Path path = Paths.get("src", "test", "resources", "stepsWithDates.csv");
		try (Reader reader = Files.newBufferedReader(path)) {
			try {
				List<String[]> list = fileReader.readAll(reader);
				if(list == null || list.isEmpty()) {
					fail("Read zero elements from file.");
				}
			} catch (IOException e) {
				fail("Exception while reading the file contents.");
			}
		} catch (IOException e) {
			fail("Failed to open source file.");
		}
	}
}
