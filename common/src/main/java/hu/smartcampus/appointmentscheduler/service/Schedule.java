package hu.smartcampus.appointmentscheduler.service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import hu.smartcampus.appointmentscheduler.domain.Event;
import hu.smartcampus.appointmentscheduler.domain.EventSchedule;
import hu.smartcampus.appointmentscheduler.domain.Period;
import hu.smartcampus.appointmentscheduler.domain.User;

public class Schedule {

	private User[] availableUsers;
	private User[] unavailableUsers;
	private int year;
	private int weekOfYear;
	private DayOfWeek dayOfWeek;
	private int hour;

	public Schedule() {
		super();
	}

	public Schedule(EventSchedule solvedEventSchedule) {
		List<Event> events = solvedEventSchedule.getEvents();
		List<Event> lockedEvents = events.stream().filter(event -> event.isLocked()).collect(Collectors.toList());
		List<Event> movableEvents = events.stream().filter(event -> !event.isLocked()).collect(Collectors.toList());

		List<User> unavailableUsers = new ArrayList<>();
		for (Event lockedEvent : lockedEvents) {
			for (Event movableEvent : movableEvents) {
				if (lockedEvent.getPeriod().equals(movableEvent.getPeriod())) {
					for (User user : movableEvent.getUsers()) {
						if (lockedEvent.getUsers().contains(user)) {
							unavailableUsers.add(user);
						}
					}
				}
			}
		}
		this.unavailableUsers = unavailableUsers.toArray(new User[unavailableUsers.size()]);

		List<User> availableUsers = new ArrayList<>(solvedEventSchedule.getUsers());
		availableUsers.removeAll(unavailableUsers);
		this.availableUsers = availableUsers.toArray(new User[availableUsers.size()]);

		this.year = solvedEventSchedule.getYear();
		this.weekOfYear = solvedEventSchedule.getWeekOfYear();

		Period period = movableEvents.get(0).getPeriod();
		this.dayOfWeek = period.getDay();
		this.hour = period.getTimeslot().getHour();
	}

	public Schedule(User[] availableUsers, User[] unavailableUsers,
					int year, int weekOfYear, DayOfWeek dayOfWeek, int hour) {
		super();
		this.availableUsers = availableUsers;
		this.unavailableUsers = unavailableUsers;
		this.year = year;
		this.weekOfYear = weekOfYear;
		this.dayOfWeek = dayOfWeek;
		this.hour = hour;
	}

	public User[] getAvailableUsers() {
		return this.availableUsers;
	}

	public void setAvailableUsers(User[] availableUsers) {
		this.availableUsers = availableUsers;
	}

	public User[] getUnavailableUsers() {
		return this.unavailableUsers;
	}

	public void setUnavailableUsers(User[] unavailableUsers) {
		this.unavailableUsers = unavailableUsers;
	}

	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getWeekOfYear() {
		return this.weekOfYear;
	}

	public void setWeekOfYear(int weekOfYear) {
		this.weekOfYear = weekOfYear;
	}

	public DayOfWeek getDayOfWeek() {
		return this.dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getHour() {
		return this.hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.availableUsers);
		result = prime * result + ((this.dayOfWeek == null) ? 0 : this.dayOfWeek.hashCode());
		result = prime * result + this.hour;
		result = prime * result + Arrays.hashCode(this.unavailableUsers);
		result = prime * result + this.weekOfYear;
		result = prime * result + this.year;
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
		if (!(obj instanceof Schedule)) {
			return false;
		}
		Schedule other = (Schedule) obj;
		if (!Arrays.equals(this.availableUsers, other.availableUsers)) {
			return false;
		}
		if (this.dayOfWeek != other.dayOfWeek) {
			return false;
		}
		if (this.hour != other.hour) {
			return false;
		}
		if (!Arrays.equals(this.unavailableUsers, other.unavailableUsers)) {
			return false;
		}
		if (this.weekOfYear != other.weekOfYear) {
			return false;
		}
		if (this.year != other.year) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Schedule [availableUsers=");
		builder.append(Arrays.toString(this.availableUsers));
		builder.append(", unavailableUsers=");
		builder.append(Arrays.toString(this.unavailableUsers));
		builder.append(", year=");
		builder.append(this.year);
		builder.append(", weekOfYear=");
		builder.append(this.weekOfYear);
		builder.append(", dayOfWeek=");
		builder.append(this.dayOfWeek);
		builder.append(", hour=");
		builder.append(this.hour);
		builder.append("]");
		return builder.toString();
	}

}
