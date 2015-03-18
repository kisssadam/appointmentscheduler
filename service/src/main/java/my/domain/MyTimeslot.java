package my.domain;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;

public class MyTimeslot implements Comparable<MyTimeslot> {

	private static int minHour = 8;
	private static int maxHour = 19;

	private int hour;

	public MyTimeslot() {
		super();
	}

	public MyTimeslot(final int hour) {
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
	public static List<MyTimeslot> getPossibleTimeslots() {
		return IntStream.rangeClosed(minHour, maxHour)
						.mapToObj(hour -> new MyTimeslot(hour))
						.distinct()
						.collect(Collectors.toList());
	}

	public static int getMinHour() {
		return minHour;
	}

	public static void setMinHour(int minHour) {
		MyTimeslot.minHour = minHour;
	}

	public static int getMaxHour() {
		return maxHour;
	}

	public static void setMaxHour(int maxHour) {
		MyTimeslot.maxHour = maxHour;
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
		builder.append("MyTimeslot [hour=");
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
		if (!(obj instanceof MyTimeslot)) {
			return false;
		}
		MyTimeslot other = (MyTimeslot) obj;
		if (this.hour != other.hour) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(MyTimeslot otherTimeslot) {
		return new CompareToBuilder().append(this.hour, otherTimeslot.hour).toComparison();
	}

}
