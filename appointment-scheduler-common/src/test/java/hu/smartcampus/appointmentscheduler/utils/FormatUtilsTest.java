package hu.smartcampus.appointmentscheduler.utils;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.time.DayOfWeek;

import org.junit.BeforeClass;
import org.junit.Test;

public class FormatUtilsTest {

	private static Timestamp timestamp;

	@BeforeClass
	public static void setUpBeforeClass() {
		timestamp = Timestamp.valueOf("2015-04-01 14:20:30"); // Wednesday, week: 14.
	}

	@Test
	public void testGetDayOfWeekFromTimeStamp() {
		DayOfWeek day = FormatUtils.getDayOfWeekFromTimeStamp(timestamp);
		assertEquals(DayOfWeek.WEDNESDAY, day);
	}

	@Test
	public void testGetWeekFromTimestamp() {
		int week = FormatUtils.getWeekFromTimestamp(timestamp);
		assertEquals(14, week);
	}

	@Test
	public void testGetYearFromTimestamp() {
		int year = FormatUtils.getYearFromTimestamp(timestamp);
		assertEquals(2015, year);
	}

	@Test
	public void testGetHourFromTimestamp() {
		int hour = FormatUtils.getHourFromTimestamp(timestamp);
		assertEquals(14, hour);
	}

	@Test
	public void testMinuteFromTimestamp() {
		int minute = FormatUtils.getMinuteFromTimestamp(timestamp);
		assertEquals(20, minute);
	}

}
