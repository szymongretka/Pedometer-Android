package pl.polsl.paum.gg.model;

import java.time.LocalTime;

public class StepRecord {
	private LocalTime time;
	private Integer stepsAmount;

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public Integer getStepsAmount() {
		return stepsAmount;
	}

	public void setStepsAmount(Integer stepsAmount) {
		this.stepsAmount = stepsAmount;
	}

	@Override
	public String toString() {
		return "StepRecord [stepsAmount=" + stepsAmount + ", time=" + time + "]";
	}

}
