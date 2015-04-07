package hu.smartcampus.appointmentscheduler.domain;

import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Contains an int value which indicates the hour of the day.
 * 
 * @author adam
 */
public class Timeslot implements Comparable<Timeslot>, Cloneable {

	/**
	 * The hour of the {@link Timeslot}.
	 */
	private int hour;

	/**
	 * Constructs a new {@link Timeslot} with hour 0.
	 */
	public Timeslot() {
		this(0);
	}

	/**
	 * Constructs a new {@link Timeslot} with the given hour argument.
	 * 
	 * @param hour the hour of the {@link Timeslot}
	 * @throws InvalidParameterException if hour is lower then 0 or bigger then 23
	 */
	public Timeslot(int hour) {
		super();
		if (hour < 0 || hour > 23) {
			String message = "Hour should be between 0 and 23, but the given value is " + hour + ".";
			throw new InvalidParameterException(message);
		}
		this.hour = hour;
	}

	/**
	 * Creates a {@link List} of {@link Timeslot} starting from (inclusive) {@code minHour} to (inclusive)
	 * {@code maxhour}.
	 * 
	 * @param minHour the lowest value of the generated list
	 * @param maxHour the highest value of the genenrated list
	 * @return a {@link List} of {@link Timeslot} starting from (inclusive) {@code minHour} to (inclusive)
	 *         {@code maxhour}
	 * @throws InvalidParameterException if {@code minhour} is bigger then {@code maxHour}
	 */
	public static List<Timeslot> createPossibleTimeslots(int minHour, int maxHour) {
		if (minHour > maxHour) {
			String message = "Argument minhour " + minHour + " is bigger then argument maxHour" + maxHour + ".";
			throw new InvalidParameterException(message);
		}
		if (minHour < 0 || maxHour > 23) {
			String message = "Arguments should be between 0 and 23, but the give values are: " + minHour + " "
					+ maxHour + ".";
			throw new InvalidParameterException(message);
		}
		return IntStream.rangeClosed(minHour, maxHour).mapToObj(hour -> new Timeslot(hour)).distinct()
				.collect(Collectors.toList());
	}

	/**
	 * Returns the hour of the {@link Timeslot}.
	 * 
	 * @return the hour of the {@link Timeslot}
	 */
	public int getHour() {
		return this.hour;
	}

	/**
	 * Sets the hour of the {@link Timeslot}.
	 * 
	 * @param hour the new hour of the {@link Timeslot}
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * Returns the {@link String} representation of the {@link Timeslot}.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Timeslot [hour=");
		builder.append(this.hour);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Returns the hash code value of the {@link Timeslot}.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.hour;
		return result;
	}

	/**
	 * Returns {@code true} if {@code obj} is equal to this {@link Timeslot}.
	 */
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

	/**
	 * Returns a shallow copy of this {@link Timeslot} instance.
	 */
	@Override
	protected Object clone() {
		return new Timeslot(this.hour);
	}

	/**
	 * Compares {@link Timeslot}s in ascending order using the {@code hour} attribute.
	 */
	@Override
	public int compareTo(Timeslot otherTimeslot) {
		return Comparator.comparing(Timeslot::getHour).compare(this, otherTimeslot);
	}

}
