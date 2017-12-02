package es.fmm.hiui.ddbb.tables.beans;

/**
 * Created by fmm on 8/2/13.
 */
public class PhoneUseBean {

	private long id;
	private String date;
	private long times;
	private long timeAmount;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getTimes() {
		return times;
	}

	public void setTimes(long times) {
		this.times = times;
	}

	public long getId() {

		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTimeAmount() {
		return timeAmount;
	}

	public void setTimeAmount(long timeAmount) {
		this.timeAmount = timeAmount;
	}

	@Override
	public String toString() {
		return date + " / " + times + " / " + timeAmount;
	}
}
