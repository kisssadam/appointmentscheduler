package hu.smartcampus.appointmentscheduler.domain;

import hu.smartcampus.appointmentscheduler.utils.FormatUtils;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Consists of a day and a timeslot.
 * 
 * @author adam
 */
public class Period implements Cloneable, Comparable<Period> {

	/**
	 * The day of the {@link Period}.
	 */
	private DayOfWeek day;

	/**
	 * The {@link Timeslot} of the period.
	 */
	private Timeslot timeslot;

	/**
	 * Constructs a new period with the following values: {@link DayOfWeek#MONDAY}, {@link Timeslot}: {@code 8}
	 */
	public Period() {
		this(DayOfWeek.MONDAY, new Timeslot(8));
	}

	/**
	 * Constructs a new {@link Period}.
	 * 
	 * @param day the day of the {@link Period}
	 * @param timeslot the timeslot of the {@link Period}
	 */
	public Period(DayOfWeek day, Timeslot timeslot) {
		super();
		this.day = day;
		this.timeslot = timeslot;
	}

	/**
	 * Creates a {@link List} of {@link Period}s. Takes the arguments and converts them to 1 hour slots.
	 * 
	 * @param eventStart the start of the event
	 * @param eventEnd theend of the event
	 * @return a {@link List} of {@link Period}
	 */
	public static List<Period> createPeriodsFromTimestamps(Timestamp eventStart, Timestamp eventEnd) {
		DayOfWeek dayOfWeek = FormatUtils.getDayOfWeekFromTimeStamp(eventStart);
		int rangeMinValue = FormatUtils.getHourFromTimestamp(eventStart);
		int rangeMaxValue = FormatUtils.getHourFromTimestamp(eventEnd);
		int eventEndMinute = FormatUtils.getMinuteFromTimestamp(eventStart);

		if (rangeMinValue == rangeMaxValue || eventEndMinute != 0) {
			rangeMaxValue++;
		}

		return IntStream.range(rangeMinValue, rangeMaxValue)
				.mapToObj(hour -> new Period(dayOfWeek, new Timeslot(hour))).distinct().collect(Collectors.toList());
	}

	/**
	 * Creates Descartes multiplication of the given arguments.
	 * 
	 * @param timeslots the timeslots to iterate over during the creation process
	 * @param daysOfWeek the days to iterate over during the creation process
	 * @return the descartes multiplication of the given arguments.
	 */
	public static List<Period> createPossiblePeriods(List<Timeslot> timeslots, List<DayOfWeek> daysOfWeek) {
		List<Period> possiblePeriods = new ArrayList<>(timeslots.size() * daysOfWeek.size());

		daysOfWeek.forEach(dayOfWeek -> {
			timeslots.forEach(timeslot -> {
				possiblePeriods.add(new Period(dayOfWeek, timeslot));
			});
		});

		return possiblePeriods;
	}

	/**
	 * Returns the day of {@link Period}.
	 * 
	 * @return the day of the {@link Period}
	 */
	public DayOfWeek getDay() {
		return this.day;
	}

	/**
	 * Sets the day of the {@link Period}.
	 * 
	 * @param day the new day of the {@link Period}
	 */
	public void setDay(DayOfWeek day) {
		this.day = day;
	}

	/**
	 * Returns the timeslot of the {@link Period}.
	 * 
	 * @return the timeslot of the {@link Period}
	 */
	public Timeslot getTimeslot() {
		return this.timeslot;
	}

	/**
	 * Sets the timeslot of the {@link Period}.
	 * 
	 * @param timeslot the new timeslot of the period
	 */
	public void setTimeslot(Timeslot timeslot) {
		this.timeslot = timeslot;
	}

	/**
	 * Returns the {@link String} representation of the {@link Period}.
	 */
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

	/**
	 * Returns the hash code value of the {@link Period}.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.day == null) ? 0 : this.day.hashCode());
		result = prime * result + ((this.timeslot == null) ? 0 : this.timeslot.hashCode());
		return result;
	}

	/**
	 * Returns {@code true} if {@code obj} is equal to this {@link Period}.
	 */
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

	/**
	 * Returns a deep copy of this {@link Period} instance.
	 */
	@Override
	protected Period clone() {
		return new Period(this.day, (Timeslot) this.timeslot.clone());
	}

	/**
	 * Compares {@link Period}s in ascending order using days and timeslots.
	 */
	@Override
	public int compareTo(Period otherPeriod) {
		return Comparator.comparing(Period::getDay).thenComparing(Period::getTimeslot).compare(this, otherPeriod);
	}

}
