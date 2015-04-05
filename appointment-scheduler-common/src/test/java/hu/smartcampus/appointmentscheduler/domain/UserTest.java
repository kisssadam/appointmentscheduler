package hu.smartcampus.appointmentscheduler.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserTest {

	private static User[] users;
	private User userToClone;
	private User clonedUser;

	@BeforeClass
	public static void setUpBeforeClass() {
		final boolean isSkippable = false;
		users = new User[] { new User("Adam", "adam", isSkippable), // 0
				new User("Adam", "adam", !isSkippable), // 1
				new User("Adam", "adamm", isSkippable), // 2
				new User("Adam", "adamm", !isSkippable), // 3
				new User("Adamm", "adam", isSkippable), // 4
				new User("Adamm", "adamm", isSkippable), // 5
				new User("Adamm", "adam", !isSkippable), // 6
				new User("Adamm", "adamm", !isSkippable) // 7
		};
	}

	@Before
	public void setUp() {
		final boolean isSkippable = false;
		this.userToClone = new User("Adam", "adam", isSkippable);
		this.clonedUser = userToClone.clone();
	}

	@Test
	public void testEquals1() {
		assertEquals(users[0], users[0]);
	}

	@Test
	public void testEquals2() {
		assertEquals(users[0], users[1]);
	}

	@Test
	public void testEquals3() {
		assertEquals(users[1], users[0]);
	}

	@Test
	public void testEquals4() {
		assertNotEquals(users[0], users[2]);
	}

	@Test
	public void testEquals5() {
		assertNotEquals(users[0], users[3]);
	}

	@Test
	public void testEquals6() {
		assertNotEquals(users[0], users[4]);
	}

	@Test
	public void testEquals7() {
		assertNotEquals(users[0], users[5]);
	}

	@Test
	public void testEquals8() {
		assertNotEquals(users[0], users[6]);
	}

	@Test
	public void testEquals9() {
		assertNotEquals(users[0], users[7]);
	}

	@Test
	public void testEquals10() {
		assertNotEquals(users[0], new Object());
	}

	@Test
	public void testEquals11() {
		assertNotEquals(users[0], null);
	}

	@Test
	public void testHashCode1() {
		assertEquals(users[0].hashCode(), users[0].hashCode());
	}

	@Test
	public void testHashCode2() {
		assertEquals(users[0].hashCode(), users[1].hashCode());
	}

	@Test
	public void testHashCode3() {
		assertEquals(users[1].hashCode(), users[0].hashCode());
	}

	@Test
	public void testHashCode4() {
		assertNotEquals(users[0].hashCode(), users[2].hashCode());
	}

	@Test
	public void testHashCode5() {
		assertNotEquals(users[0].hashCode(), users[3].hashCode());
	}

	@Test
	public void testHashCode6() {
		assertNotEquals(users[0].hashCode(), users[4].hashCode());
	}

	@Test
	public void testHashCode7() {
		assertNotEquals(users[0].hashCode(), users[5].hashCode());
	}

	@Test
	public void testHashCode8() {
		assertNotEquals(users[0].hashCode(), users[6].hashCode());
	}

	@Test
	public void testHashCode9() {
		assertNotEquals(users[0].hashCode(), users[7].hashCode());
	}

	@Test
	public void testHashCode10() {
		assertNotEquals(users[0].hashCode(), new Object().hashCode());
	}

	@Test
	public void testHashCode11() {
		assertNotEquals(users[0].hashCode(), null);
	}

	@Test
	public void testClone1() {
		assertNotSame(userToClone, clonedUser);
	}

	@Test
	public void testClone2() {
		assertEquals(userToClone, clonedUser);
	}

	@Test
	public void testClone3() {
		assertSame(userToClone.getDisplayName(), clonedUser.getDisplayName());
	}

	@Test
	public void testClone4() {
		assertSame(userToClone.getLoginName(), clonedUser.getLoginName());
	}
	
}
