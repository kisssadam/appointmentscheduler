package hu.smartcampus.appointmentscheduler.domain;

import java.security.InvalidParameterException;
import java.util.Comparator;

public class Timeslot implements Comparable<Timeslot>, Cloneable {

	private int hour;

	public Timeslot() {
		this(0);
	}

	public Timeslot(int hour) {
		super();
		if (hour < 0 || hour > 23) {
			StringBuilder sb = new StringBuilder(60);
			sb.append("Hour should be between 0 and 23, but the given value is ");
			sb.append(hour);
			sb.append(".");
			throw new InvalidParameterException(sb.toString());
		}
		this.hour = hour;
	}

	public int getHour() {
		return this.hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Timeslot [hour=");
		builder.append(this.hour);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.hour;
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
		if (!(obj instanceof Timeslot)) {
			return false;
		}
		Timeslot other = (Timeslot) obj;
		if (this.hour != other.hour) {
			return false;
		}
		return true;
	}

	@Override
	protected Object clone() {
		return new Timeslot(this.hour);
	}

	@Override
	public int compareTo(Timeslot otherTimeslot) {
		return Comparator.comparing(Timeslot::getHour).compare(this, otherTimeslot);
	}

}
