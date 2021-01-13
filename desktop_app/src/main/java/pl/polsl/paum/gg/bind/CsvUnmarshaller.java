package pl.polsl.paum.gg.bind;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import com.opencsv.CSVReader;

import pl.polsl.paum.gg.model.DailyStepRecord;
import pl.polsl.paum.gg.model.PedometerCsv;
import pl.polsl.paum.gg.model.StepRecord;

public class CsvUnmarshaller {

	private LocalTime[] convertStringsToLocalTimes(String[] strings, int startingIndex) {
		LocalTime[] localTimes = new LocalTime[strings.length - startingIndex];
		for (int i = startingIndex; i < strings.length; ++i) {
			localTimes[i - startingIndex] = LocalTime.parse(strings[i].trim());
		}
		return localTimes;
	}

	public PedometerCsv unmarshalCsvToPojo(Reader sourceCsvFile) {
		if (sourceCsvFile == null) {
			return null;
		}
		try (CSVReader csvReader = new CSVReader(sourceCsvFile)) {
			PedometerCsv pedometerCsv = new PedometerCsv();
			pedometerCsv.setColumnsLabels(csvReader.readNext()); // first line contains labels
			LocalTime[] timesArray = convertStringsToLocalTimes(pedometerCsv.getColumnsLabels(), 1);
			List<DailyStepRecord> dailyStepRecordList = new LinkedList<>();
			List<StepRecord> stepRecordList;
			DailyStepRecord dailyStepRecord;
			StepRecord stepRecord;
			String[] line;
			while (((line = csvReader.readNext()) != null)) {
				dailyStepRecord = new DailyStepRecord();
				dailyStepRecord.setDate(LocalDate.parse(line[0]));
				stepRecordList = new LinkedList<>();
				for (int i = 1; i < line.length; ++i) {
					stepRecord = new StepRecord();
					stepRecord.setStepsAmount(Integer.parseInt(line[i].trim()));
					stepRecord.setTime(timesArray[i - 1]);
					stepRecordList.add(stepRecord);
				}
				dailyStepRecord.setStepRecordList(stepRecordList);
				dailyStepRecordList.add(dailyStepRecord);
			}
			pedometerCsv.setDailyStepRecordList(dailyStepRecordList);
			return pedometerCsv;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
