package es.fmm.hiui.ddbb.tables.beans;

/**
 * Created by fmm on 7/31/13.
 */
public class AppUseBean {

	private long id;
	private String date;
	private String app;
	private long rate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public long getRate() {
		return rate;
	}

	public void setRate(long rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		return date + " / " + app + " / " + rate;
	}
}
