package de.leotuet.models;

public class TimeSlot {
	private final int id;
	private final String weekDay;
	private final int start;
	private final int end;

	public TimeSlot(int id, String weekDay, int start, int end) {
		this.id = id;
		this.weekDay = weekDay;
		this.start = start;
		this.end = end;
	}

	public int getId() {
		return id;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "TimeSlot{" +
				"id=" + id +
				", weekDay='" + weekDay + '\'' +
				", start=" + start +
				", end=" + end +
				'}';
	}
}
