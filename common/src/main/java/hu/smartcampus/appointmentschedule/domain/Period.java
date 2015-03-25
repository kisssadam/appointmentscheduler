package hu.smartcampus.appointmentschedule.domain;

import java.time.DayOfWeek;
import java.util.Comparator;

public class Period implements Cloneable, Comparable<Period> {

	private DayOfWeek day;
	private Timeslot timeslot;

	public Period() {
		super();
	}

	public Period(DayOfWeek day, Timeslot timeslot) {
		super();
		this.day = day;
		this.timeslot = timeslot;
	}

	public DayOfWeek getDay() {
		return this.day;
	}

	public void setDay(DayOfWeek day) {
		this.day = day;
	}

	public Timeslot getTimeslot() {
		return this.timeslot;
	}

	public void setTimeslot(Timeslot timeslot) {
		this.timeslot = timeslot;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Period [");
		builder.append(this.day);
		builder.append(", ");
		builder.append(this.timeslot.getHour());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.day == null) ? 0 : this.day.hashCode());
		result = prime * result + ((this.timeslot == null) ? 0 : this.timeslot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Period)) {
			return false;
		}
		Period other = (Period) obj;
		if (this.day != other.day) {
			return false;
		}
		if (this.timeslot == null) {
			if (other.timeslot != null) {
				return false;
			}
		} else if (!this.timeslot.equals(other.timeslot)) {
			return false;
		}
		return true;
	}

	@Override
	protected Period clone() {
		return new Period(this.day, (Timeslot) this.timeslot.clone());
	}

	@Override
	public int compareTo(Period otherPeriod) {
		return Comparator.comparing(Period::getDay).thenComparing(Period::getTimeslot).compare(this, otherPeriod);
	}

}
