package hu.smartcampus.appointmentscheduler.utils;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public abstract class FormatUtils {

	private static final ZoneId budapestZoneId;
	private static final DateTimeFormatter dayOfWeekDateTimeFormatter;
	private static final DateTimeFormatter weekDateTimeFormatter;
	private static final DateTimeFormatter yearDateTimeFormatter;
	private static final DateTimeFormatter hourDateTimeFormatter;
	private static final DateTimeFormatter minuteDateTimeFormatter;

	static {
		budapestZoneId = ZoneId.of("Europe/Budapest");

		dayOfWeekDateTimeFormatter = DateTimeFormatter.ofPattern("EEEE");
		weekDateTimeFormatter = DateTimeFormatter.ofPattern("w");
		yearDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy");
		hourDateTimeFormatter = DateTimeFormatter.ofPattern("H");
		minuteDateTimeFormatter = DateTimeFormatter.ofPattern("m");
	}

	public static DayOfWeek getDayOfWeekFromTimeStamp(Timestamp timestamp) {
		return DayOfWeek
				.valueOf(getBudapestZonedDateTime(timestamp).format(dayOfWeekDateTimeFormatter).toUpperCase());
	}

	public static int getWeekFromTimestamp(Timestamp timestamp) {
		return Integer.parseInt(getBudapestZonedDateTime(timestamp).format(weekDateTimeFormatter));
	}

	public static int getYearFromTimestamp(Timestamp timestamp) {
		return Integer.parseInt(getBudapestZonedDateTime(timestamp).format(yearDateTimeFormatter));
	}

	public static int getHourFromTimestamp(Timestamp timestamp) {
		return Integer.parseInt(getBudapestZonedDateTime(timestamp).format(hourDateTimeFormatter));
	}

	public static int getMinuteFromTimestamp(Timestamp timestamp) {
		return Integer.parseInt(getBudapestZonedDateTime(timestamp).format(minuteDateTimeFormatter));
	}

	private static ZonedDateTime getBudapestZonedDateTime(Timestamp timestamp) {
		return timestamp.toInstant().atZone(budapestZoneId);
	}

}
