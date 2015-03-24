package hu.smartcampus.appointmentschedule.domain;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;

public class Timeslot implements Comparable<Timeslot> {

	private static int minHour = 8;
	private static int maxHour = 19;

	private int hour;

	public Timeslot() {
		super();
	}

	public Timeslot(int hour) {
		super();
		if (hour < minHour || hour > maxHour) {
			StringBuilder sb = new StringBuilder(60);
			sb.append("Hour should be between ");
			sb.append(minHour);
			sb.append(" and ");
			sb.append(maxHour);
			sb.append(", but the given value is ");
			sb.append(hour);
			sb.append(".");
			throw new IndexOutOfBoundsException(sb.toString());
		}
		this.hour = hour;
	}

	@ValueRangeProvider(id = "periodRange")
	public static List<Timeslot> getPossibleTimeslots() {
		return IntStream.rangeClosed(minHour, maxHour)
						.mapToObj(hour -> new Timeslot(hour))
						.distinct()
						.collect(Collectors.toList());
	}

	public static int getMinHour() {
		return minHour;
	}

	public static void setMinHour(int minHour) {
		Timeslot.minHour = minHour;
	}

	public static int getMaxHour() {
		return maxHour;
	}

	public static void setMaxHour(int maxHour) {
		Timeslot.maxHour = maxHour;
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
	public int compareTo(Timeslot otherTimeslot) {
		return new CompareToBuilder().append(this.hour, otherTimeslot.hour).toComparison();
	}

}
