package hu.smartcampus.appointmentscheduler.domain;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class EventTest {

	private static Event eventToClone;
	private static Event clonedEvent;

	@BeforeClass
	public static void setUpBeforeClass() {
		String title = "Szakdolgozat";
		Period period = new Period(DayOfWeek.MONDAY, new Timeslot(8));

		List<User> users = new ArrayList<User>();
		boolean skippable = true;
		users.add(new User("Adam", "adam", skippable));
		users.add(new User("Peter", "peter", skippable));
		users.add(new User("David", "david", skippable));

		boolean locked = true;

		eventToClone = new Event(title, period, users, locked);
		clonedEvent = eventToClone.clone();
	}

	/**
	 * The cloned {@link Event} should be a new instance.
	 */
	@Test
	public void testClone1() {
		assertNotSame(eventToClone, clonedEvent);
	}

	/**
	 * The {@link Period} of the new {@link Event} should be a new instance.
	 */
	@Test
	public void testClone2() {
		assertNotSame(eventToClone.getPeriod(), clonedEvent.getPeriod());
	}

	/**
	 * The {@code title} of the cloned {@link Event} should refer to the original {@code title}.
	 */
	@Test
	public void testClone3() {
		assertSame(eventToClone.getTitle(), clonedEvent.getTitle());
	}

	/**
	 * The list of users of the cloned {@link Event} should refer to the original list.
	 */
	@Test
	public void testClone4() {
		assertSame(eventToClone.getUsers(), clonedEvent.getUsers());
	}

	/**
	 * The cloned {@link Event} should be equal to the original {@link Event}.
	 */
	@Test
	public void testClone5() {
		assertEquals(eventToClone, clonedEvent);
	}

}
