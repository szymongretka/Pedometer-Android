package pl.polsl.paum.gg.model;

import java.util.Arrays;
import java.util.List;

public class PedometerCsv {

	private String[] columnsLabels;
	private List<DailyStepRecord> dailyStepRecordList; // verical

	public String[] getColumnsLabels() {
		return columnsLabels;
	}

	public void setColumnsLabels(String[] columnsLabels) {
		this.columnsLabels = columnsLabels;
	}

	public List<DailyStepRecord> getDailyStepRecordList() {
		return dailyStepRecordList;
	}

	public void setDailyStepRecordList(List<DailyStepRecord> dailyStepRecordList) {
		this.dailyStepRecordList = dailyStepRecordList;
	}

	@Override
	public String toString() {
		return "PedometerCsv [columnsLabels=" + Arrays.toString(columnsLabels) + ", dailyStepRecordList="
				+ dailyStepRecordList + "]";
	}

}
