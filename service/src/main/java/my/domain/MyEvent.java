package my.domain;

import java.io.Serializable;
import java.util.List;

import my.domain.solver.EventDifficultyComparator;
import my.domain.solver.MovableEventSelectionFilter;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity(difficultyComparatorClass = EventDifficultyComparator.class,
				movableEntitySelectionFilter = MovableEventSelectionFilter.class)
@XStreamAlias("MyEvent")
public class MyEvent implements Serializable, Comparable<MyEvent>, Cloneable {
	
	private static final long serialVersionUID = 1L;
	private String title;
	private MyPeriod period;
	private List<User> users;
	private boolean locked;

	public MyEvent() {
		super();
	}

	public MyEvent(String title, MyPeriod period, List<User> users, boolean locked) {
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
	
	@PlanningVariable(valueRangeProviderRefs = {"periodRange"})
	public MyPeriod getPeriod() {
		return this.period;
	}

	public void setPeriod(MyPeriod period) {
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
		if (!(obj instanceof MyEvent)) {
			return false;
		}
		MyEvent other = (MyEvent) obj;
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
		builder.append("MyEvent [title=");
		builder.append(this.title);
		builder.append(", period=");
		builder.append(this.period);
		builder.append(", locked=");
		builder.append(this.locked);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(MyEvent otherEvent) {
		return new CompareToBuilder().append(this, otherEvent).toComparison();
	}

	@Override
	protected MyEvent clone() {
		return new MyEvent(this.title, this.period, this.users, this.locked); 
	}
	
}
