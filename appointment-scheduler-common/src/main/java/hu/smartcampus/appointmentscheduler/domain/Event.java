package hu.smartcampus.appointmentscheduler.domain;

import hu.smartcampus.appointmentscheduler.domain.solver.EventDifficultyComparator;
import hu.smartcampus.appointmentscheduler.domain.solver.MovableEventSelectionFilter;
import hu.smartcampus.appointmentscheduler.domain.solver.PeriodStrengthComparator;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Holds information about events.
 * 
 * @author adam
 */
@PlanningEntity(difficultyComparatorClass = EventDifficultyComparator.class,
		movableEntitySelectionFilter = MovableEventSelectionFilter.class)
public class Event implements Serializable, Comparable<Event>, Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * The title of the {@link Event}.
	 */
	private String title;

	/**
	 * The {@link Period} of the {@link Event}. This is a {@link PlanningVariable}. See the description of
	 * {@link #getPeriod()}.
	 */
	private Period period;

	/**
	 * The {@link User}s of the {@link Event}.
	 */
	private List<User> users;

	/**
	 * Indicates is this {@link Event} movable by the planning algorithm or not. If {@code true} then the planning
	 * algorithm will not move this {@link Event}.
	 */
	private boolean locked;

	/**
	 * Constructs an empty {@link Event}.
	 */
	public Event() {
		super();
	}

	/**
	 * Constructs an {@link Event} with the given arguments.
	 * 
	 * @param title the title of the {@link Event}.
	 * @param period the period of the {@link Event}.
	 * @param users the users of the {@link Event}.
	 * @param locked indicates is this {@link Event} movable by the planning algorithm or not
	 */
	public Event(String title, Period period, List<User> users, boolean locked) {
		super();
		this.title = title;
		this.period = period;
		this.users = users;
		this.locked = locked;
	}

	/**
	 * Returns the title of the {@link Event}.
	 * 
	 * @return the title of the event
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the title of the {@link Event}.
	 * 
	 * @param title the new title of the {@link Event}
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the period of the {@link Event}. This is {@link PlanningVariable}. If {@link #locked} is {@code false }
	 * then this variable can be changed by the planning algorithm.
	 * 
	 * @return the period of the event
	 */
	@PlanningVariable(valueRangeProviderRefs = { "periodRange" },
			strengthComparatorClass = PeriodStrengthComparator.class)
	public Period getPeriod() {
		return this.period;
	}

	/**
	 * Sets the period of the {@link Event}.
	 * 
	 * @param period the new {@link Period} of the {@link Event}
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	/**
	 * Returns the {@link User}s of the {@link Event}.
	 * 
	 * @return a {@link List} containing {@link User}s of the {@link Event}
	 */
	public List<User> getUsers() {
		return this.users;
	}

	/**
	 * Sets the {@link User}s of the {@link Event}.
	 * 
	 * @param users the new users of the {@link Event}
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * Returns {@code true} if {@link Event} is locked.
	 * 
	 * @return {@code true} if this {@link Event} is locked; {@code false} otherwise
	 */
	public boolean isLocked() {
		return this.locked;
	}

	/**
	 * Sets {@link #isLocked()}Locked variable of {@link Event}.
	 * 
	 * @param locked the new value of {@link #isLocked()}
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * Returns the hash code value of the {@link Event}.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.locked ? 1231 : 1237);
		result = prime * result + ((this.period == null) ? 0 : this.period.hashCode());
		result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
		result = prime * result + ((this.users == null) ? 0 : this.users.hashCode());
		return result;
	}

	/**
	 * Returns {@code true} if {@code obj} is equal to this {@link Event}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Event)) {
			return false;
		}
		Event other = (Event) obj;
		if (this.locked != other.locked) {
			return false;
		}
		if (this.period == null) {
			if (other.period != null) {
				return false;
			}
		} else if (!this.period.equals(other.period)) {
			return false;
		}
		if (this.title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!this.title.equals(other.title)) {
			return false;
		}
		if (this.users == null) {
			if (other.users != null) {
				return false;
			}
		} else if (!this.users.equals(other.users)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the {@link String} representation of the {@link Event}.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Event [title=");
		builder.append(this.title);
		builder.append(", period=");
		builder.append(this.period);
		builder.append(", users=");
		builder.append(this.users);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * This method is only used during debugging. When
	 * {@link EventScheduleFactory#newEventSchedule(String[], String[], DayOfWeek[], int, int, int, int)} creates the
	 * events then we can print them out to the console.
	 */
	@Override
	public int compareTo(Event otherEvent) {
		return Comparator.comparing(Event::getPeriod).thenComparing(Event::getTitle).compare(this, otherEvent);
	}

	/**
	 * Returns a new {@link Event}. Creates a deep copy of {@link #period} and shallow copy of {@link #title}
	 * {@link #users} {@code locked}.
	 */
	@Override
	protected Event clone() {
		return new Event(this.title, this.period.clone(), this.users, this.locked);
	}

}
