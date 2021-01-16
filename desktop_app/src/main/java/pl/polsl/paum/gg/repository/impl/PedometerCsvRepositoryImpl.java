package pl.polsl.paum.gg.repository.impl;

import java.time.LocalDate;

import pl.polsl.paum.gg.model.DailyStepRecord;
import pl.polsl.paum.gg.model.PedometerCsv;
import pl.polsl.paum.gg.repository.PedometerCsvRepository;

public class PedometerCsvRepositoryImpl implements PedometerCsvRepository{
	
	private PedometerCsv pedometerCsv;

	public PedometerCsvRepositoryImpl() {
		
	}
	
	public PedometerCsvRepositoryImpl(PedometerCsv pedometerCsv) {
		super();
		this.pedometerCsv = pedometerCsv;
	}
	
	@Override
	public void setSource(PedometerCsv pedometerCsv) {
		this.pedometerCsv = pedometerCsv;
	}
	
	@Override
	public DailyStepRecord findDailyStepRecordByLocalDate(LocalDate date) {
		if(isEmpty()) {
			return null;
		}
		for(DailyStepRecord dailyStepRecord : pedometerCsv.getDailyStepRecordList()) {
			if(dailyStepRecord.getDate().isEqual(date)) {
				return dailyStepRecord;
			}
		}
		return null;
	}
	
	@Override
	public DailyStepRecord findDailyStepRecordByIndex(int index) {
		if(isEmpty()) {
			return null;
		}
		return pedometerCsv.getDailyStepRecordList().get(index);
	}
	
	@Override
	public boolean isEmpty() {
		if(pedometerCsv == null || pedometerCsv.getDailyStepRecordList() == null) {
			return true;
		}
		return false;
	}
	
	
}
