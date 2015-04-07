package hu.smartcampus.appointmentscheduler.domain;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;
import java.util.List;

import org.junit.Test;

public class TimeslotTest {

	@Test(expected = InvalidParameterException.class)
	public void testConstructor1() {
		new Timeslot(-1);
	}

	@Test(expected = InvalidParameterException.class)
	public void testConstructor2() {
		new Timeslot(24);
	}

	@Test()
	public void testConstructor3() {
		new Timeslot(0);
	}

	@Test()
	public void testConstructor4() {
		new Timeslot(23);
	}

	@Test
	public void testCreatePossibleTimeslots1() {
		List<Timeslot> timeslots = Timeslot.createPossibleTimeslots(0, 23);
		assertEquals(24, timeslots.size());
	}

	@Test(expected = InvalidParameterException.class)
	public void testCreatePossibleTimeslots2() {
		Timeslot.createPossibleTimeslots(23, 0);
	}

	@Test(expected = InvalidParameterException.class)
	public void testCreatePossibleTimeslots3() {
		Timeslot.createPossibleTimeslots(-1, 23);
	}

	@Test(expected = InvalidParameterException.class)
	public void testCreatePossibleTimeslots4() {
		Timeslot.createPossibleTimeslots(0, 24);
	}

}
