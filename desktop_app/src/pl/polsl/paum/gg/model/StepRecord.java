package pl.polsl.paum.gg.model;

import java.time.OffsetTime;

public class StepRecord {
	private OffsetTime time;
	private Integer stepsAmount;

	public OffsetTime getTime() {
		return time;
	}

	public void setTime(OffsetTime time) {
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
