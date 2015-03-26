package hu.smartcampus.appointmentscheduler.domain;

import hu.smartcampus.appointmentscheduler.domain.solver.EventDifficultyComparator;
import hu.smartcampus.appointmentscheduler.domain.solver.MovableEventSelectionFilter;
import hu.smartcampus.appointmentscheduler.domain.solver.PeriodStrengthWeightFactory;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity(difficultyComparatorClass = EventDifficultyComparator.class,
				movableEntitySelectionFilter = MovableEventSelectionFilter.class)
public class Event implements Serializable, Comparable<Event>, Cloneable {
	
	private static final long serialVersionUID = 1L;
	private String title;
	private Period period;
	private List<User> users;
	private boolean locked;

	public Event() {
		super();
	}

	public Event(String title, Period period, List<User> users, boolean locked) {
		super();
		this.title = title;
		this.period = period;
		this.users = users;
		this.locked = locked;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@PlanningVariable(valueRangeProviderRefs = {"periodRange"},
					  strengthWeightFactoryClass = PeriodStrengthWeightFactory.class)
	public Period getPeriod() {
		return this.period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	@Override
	public int hashCode() {
		final int prime = 127;
		int result = 1;
		result = prime * result + ((this.period == null) ? 0 : this.period.hashCode());
		result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
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
		if (!(obj instanceof Event)) {
			return false;
		}
		Event other = (Event) obj;
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
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Event [title=");
		builder.append(this.title);
		builder.append(", period=");
		builder.append(this.period);
		builder.append(", locked=");
		builder.append(this.locked);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * This method is only used during debugging. When {@link EventSchedule#createEventSchedule(String[], String[], int, int, DayOfWeek[])}
	 * creates the events then we can print them out to the console.
	 */
	@Override
	public int compareTo(Event otherEvent) {
		return Comparator.comparing(Event::getPeriod).thenComparing(Event::getTitle).compare(this, otherEvent);
	}

	@Override
	protected Event clone() {
		return new Event(this.title, this.period.clone(), this.users, this.locked); 
	}
	
}
