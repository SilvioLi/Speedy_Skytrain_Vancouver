package com.speedy.skytrain;

public class FirstLastTrain {

	private boolean isFirst;
	private String line;
	private String to;
	private String from;
	private String week;
	private String sat;
	private String holiday;
	
	
	public FirstLastTrain() {
	}
	
	public FirstLastTrain(boolean first, String line, String to, String from, String week, String sat, String holiday){
		this.isFirst = first;
		this.setLine(line);
		this.setTo(to);
		this.setFrom(from);
		this.setWeek(week);
		this.setSat(sat);
		this.setHoliday(holiday);
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getSat() {
		return sat;
	}

	public void setSat(String sat) {
		this.sat = sat;
	}

	public String getHoliday() {
		return holiday;
	}

	public void setHoliday(String holiday) {
		this.holiday = holiday;
	}

}
