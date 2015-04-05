package hu.smartcampus.appointmentscheduler.domain;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

	private User user1;
	private User user2;

	@Before
	public void setUp() {
		boolean isSkippable = false;
		user1 = new User("Adam", "adam", isSkippable);
		user2 = new User("Adam", "adam", !isSkippable);
	}

	@Test
	public void testEquals() {
//		Assert
	}

}
