package hu.smartcampus.appointmentschedule.domain;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class MyPeriod implements Cloneable, Comparable<MyPeriod> {

	private MyDay day;
	private MyTimeslot timeslot;

	public MyPeriod() {
		super();
	}

	public MyPeriod(MyDay day, MyTimeslot timeslot) {
		super();
		this.day = day;
		this.timeslot = timeslot;
	}

	public MyDay getDay() {
		return this.day;
	}

	public void setDay(MyDay day) {
		this.day = day;
	}

	public MyTimeslot getTimeslot() {
		return this.timeslot;
	}

	public void setTimeslot(MyTimeslot timeslot) {
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
		if (!(obj instanceof MyPeriod)) {
			return false;
		}
		MyPeriod other = (MyPeriod) obj;
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
	protected MyPeriod clone() {
		return new MyPeriod(this.day, this.timeslot);
	}

	@Override
	public int compareTo(MyPeriod otherPeriod) {
		return new CompareToBuilder().append(this.day, otherPeriod.day).append(this.timeslot, otherPeriod.timeslot).toComparison();
	}

}
