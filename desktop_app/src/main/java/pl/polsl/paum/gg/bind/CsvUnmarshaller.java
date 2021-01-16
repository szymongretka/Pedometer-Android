package pl.polsl.paum.gg.bind;

import java.io.Reader;

import pl.polsl.paum.gg.exception.ConversionException;
import pl.polsl.paum.gg.model.PedometerCsv;

public interface CsvUnmarshaller {

	PedometerCsv unmarshalCsvToPojo(Reader sourceCsvFile) throws ConversionException;

}
