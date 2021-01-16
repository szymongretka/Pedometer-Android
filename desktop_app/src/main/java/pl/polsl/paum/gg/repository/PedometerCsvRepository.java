package pl.polsl.paum.gg.repository;

import java.time.LocalDate;
import java.util.List;

import pl.polsl.paum.gg.model.DailyStepRecord;
import pl.polsl.paum.gg.model.PedometerCsv;

public interface PedometerCsvRepository {

	DailyStepRecord findDailyStepRecordByLocalDate(LocalDate date);

	void setSource(PedometerCsv pedometerCsv);

	boolean isEmpty();

	DailyStepRecord findDailyStepRecordByIndex(int index);

	List<DailyStepRecord> findAllDailyStepRecords();

}
