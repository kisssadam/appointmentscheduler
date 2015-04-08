package hu.smartcampus.appointmentscheduler.utils;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains method to format {@link Timestamp} objects.
 * 
 * @author adam
 */
public abstract class FormatUtils {

	/**
	 * Holds {@link ZoneId} of "Europe/Budapest". {@link #getBudapestZonedDateTime(Timestamp)} converts timestamp to
	 * {@link ZonedDateTime}.
	 */
	private static final ZoneId budapestZoneId;

	/**
	 * {@link #getDayOfWeekFromTimeStamp(Timestamp)} uses to return the {@link DayOfWeek} of {@link Timestamp}.
	 */
	private static final DateTimeFormatter dayOfWeekDateTimeFormatter;

	/**
	 * {@link #getWeekFromTimestamp(Timestamp)} uses to return the number of week of {@link Timestamp}.
	 */
	private static final DateTimeFormatter weekDateTimeFormatter;

	/**
	 * {@link #getYearFromTimestamp(Timestamp)} uses to return the year of {@link Timestamp}.
	 */
	private static final DateTimeFormatter yearDateTimeFormatter;

	/**
	 * {@link #getHourFromTimestamp(Timestamp)} uses to return the hour of {@link Timestamp}.
	 */
	private static final DateTimeFormatter hourDateTimeFormatter;

	/**
	 * {@link #getMinuteFromTimestamp(Timestamp)} uses to return the minute of {@link Timestamp}.
	 */
	private static final DateTimeFormatter minuteDateTimeFormatter;

	static {
		budapestZoneId = ZoneId.of("Europe/Budapest");

		dayOfWeekDateTimeFormatter = DateTimeFormatter.ofPattern("EEEE");
		weekDateTimeFormatter = DateTimeFormatter.ofPattern("w");
		yearDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy");
		hourDateTimeFormatter = DateTimeFormatter.ofPattern("H");
		minuteDateTimeFormatter = DateTimeFormatter.ofPattern("m");
	}

	/**
	 * Returns the {@link DayOfWeek} of {@code timestamp} using {@link #budapestZoneId}.
	 * 
	 * @param timestamp the {@link Timestamp} to format
	 * @return {@link DayOfWeek} of {@code timestamp}
	 */
	public static DayOfWeek getDayOfWeekFromTimeStamp(Timestamp timestamp) {
		return DayOfWeek
				.valueOf(getBudapestZonedDateTime(timestamp).format(dayOfWeekDateTimeFormatter).toUpperCase());
	}

	/**
	 * Returns the week number of {@code timestamp} using {@link #budapestZoneId}.
	 * 
	 * @param timestamp the {@link Timestamp} to format
	 * @return the week number of {@code timestamp}
	 */
	public static int getWeekFromTimestamp(Timestamp timestamp) {
		return Integer.parseInt(getBudapestZonedDateTime(timestamp).format(weekDateTimeFormatter));
	}

	/**
	 * Returns the year of {@code timestamp} using {@link #budapestZoneId}.
	 * 
	 * @param timestamp the {@link Timestamp} to format
	 * @return the year of {@code timestamp}
	 */
	public static int getYearFromTimestamp(Timestamp timestamp) {
		return Integer.parseInt(getBudapestZonedDateTime(timestamp).format(yearDateTimeFormatter));
	}

	/**
	 * Returns the hour of {@code timestamp} using {@link #budapestZoneId}.
	 * 
	 * @param timestamp the {@link Timestamp} to format
	 * @return the hour of {@code timestamp}
	 */
	public static int getHourFromTimestamp(Timestamp timestamp) {
		return Integer.parseInt(getBudapestZonedDateTime(timestamp).format(hourDateTimeFormatter));
	}

	/**
	 * Returns the minute of {@code timestamp} using {@link #budapestZoneId}.
	 * 
	 * @param timestamp the {@link Timestamp} to format
	 * @return the minute of {@code timestamp}
	 */
	public static int getMinuteFromTimestamp(Timestamp timestamp) {
		return Integer.parseInt(getBudapestZonedDateTime(timestamp).format(minuteDateTimeFormatter));
	}

	/**
	 * Converts {@code timestamp} using {@link #budapestZoneId} to {@link ZonedDateTime} and returns it.
	 * 
	 * @param timestamp the {@link Timestamp} to format
	 * @return timestamp in {@link ZonedDateTime}
	 */
	private static ZonedDateTime getBudapestZonedDateTime(Timestamp timestamp) {
		return timestamp.toInstant().atZone(budapestZoneId);
	}

}
