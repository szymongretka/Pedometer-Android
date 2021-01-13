package pl.polsl.paum.gg.model;

import java.time.LocalDate;
import java.util.List;

public class DailyStepRecord {

	private LocalDate date;
	private List<StepRecord> stepRecordList; //horizontal

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<StepRecord> getStepRecordList() {
		return stepRecordList;
	}

	public void setStepRecordList(List<StepRecord> stepRecordList) {
		this.stepRecordList = stepRecordList;
	}

	@Override
	public String toString() {
		return "DailyStepRecord [date=" + date + ", stepRecordList=" + stepRecordList + "]";
	}

}
