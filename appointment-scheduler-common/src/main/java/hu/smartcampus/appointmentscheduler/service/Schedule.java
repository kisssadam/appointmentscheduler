package hu.smartcampus.appointmentscheduler.service;

import hu.smartcampus.appointmentscheduler.domain.Event;
import hu.smartcampus.appointmentscheduler.domain.EventSchedule;
import hu.smartcampus.appointmentscheduler.domain.Period;
import hu.smartcampus.appointmentscheduler.domain.User;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Schedule implements Serializable {

	private static final long serialVersionUID = 1L;
	private User[] availableUsers;
	private User[] unavailableUsers;
	private Date date;

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
		Arrays.sort(this.unavailableUsers);

		List<User> availableUsers = new ArrayList<>(solvedEventSchedule.getUsers());
		availableUsers.removeAll(unavailableUsers);
		this.availableUsers = availableUsers.toArray(new User[availableUsers.size()]);
		Arrays.sort(this.availableUsers);

		int year = solvedEventSchedule.getYear();
		int weekOfYear = solvedEventSchedule.getWeekOfYear();

		Period period = movableEvents.get(0).getPeriod();
		DayOfWeek dayOfWeek = period.getDay();
		int hour = period.getTimeslot().getHour();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-ww-EEEE-H");
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append("-");
		sb.append(weekOfYear);
		sb.append("-");
		sb.append(dayOfWeek);
		sb.append("-");
		sb.append(hour);
		Date date;
		try {
			date = simpleDateFormat.parse(sb.toString());
		} catch (ParseException e) {
			date = new Date();
			e.printStackTrace();
		}
		this.date = date;
	}

	public Schedule(User[] availableUsers, User[] unavailableUsers, Date date) {
		super();
		this.availableUsers = availableUsers;
		this.unavailableUsers = unavailableUsers;
		this.date = date;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(availableUsers);
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + Arrays.hashCode(unavailableUsers);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Schedule other = (Schedule) obj;
		if (!Arrays.equals(availableUsers, other.availableUsers))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (!Arrays.equals(unavailableUsers, other.unavailableUsers))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Schedule [availableUsers=");
		builder.append(Arrays.toString(availableUsers));
		builder.append(", unavailableUsers=");
		builder.append(Arrays.toString(unavailableUsers));
		builder.append(", date=");
		builder.append(date);
		builder.append("]");
		return builder.toString();
	}

}
