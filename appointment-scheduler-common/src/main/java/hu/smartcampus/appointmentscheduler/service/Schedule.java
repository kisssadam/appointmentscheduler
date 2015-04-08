package hu.smartcampus.appointmentscheduler.service;

import hu.smartcampus.appointmentscheduler.domain.User;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Schedule implements Serializable {

	private static final long serialVersionUID = 1L;
	private User[] availableUsers;
	private User[] unavailableUsers;
	private Date date;

	public Schedule() {
		super();
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
