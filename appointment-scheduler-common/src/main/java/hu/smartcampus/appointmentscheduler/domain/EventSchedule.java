package hu.smartcampus.appointmentscheduler.domain;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

@PlanningSolution
public class EventSchedule implements Solution<HardMediumSoftScore>, PlanningCloneable<EventSchedule> {

	private List<Period> possiblePeriods;
	private List<DayOfWeek> daysOfWeek;
	private int year;
	private int weekOfYear;
	private int minHour;
	private int maxHour;
	private List<User> users;
	private List<Event> events;
	private HardMediumSoftScore score;

	protected EventSchedule() {
		super();
	}

	public EventSchedule(List<Period> possiblePeriods, List<DayOfWeek> daysOfWeek, int year, int weekOfYear,
			int minHour, int maxHour, List<User> users, List<Event> events, HardMediumSoftScore score) {
		super();
		this.daysOfWeek = daysOfWeek;
		this.year = year;
		this.weekOfYear = weekOfYear;
		this.minHour = minHour;
		this.maxHour = maxHour;
		this.users = users;
		this.events = events;
		this.score = score;
	}

	@ValueRangeProvider(id = "periodRange")
	public List<Period> getPossiblePeriods() {
		return this.possiblePeriods;
	}

	public void setPossiblePeriods(List<Period> possiblePeriods) {
		this.possiblePeriods = possiblePeriods;
	}

	public List<DayOfWeek> getDaysOfWeek() {
		return this.daysOfWeek;
	}

	public void setDaysOfWeek(List<DayOfWeek> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
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

	public int getMinHour() {
		return this.minHour;
	}

	public void setMinHour(int minHour) {
		this.minHour = minHour;
	}

	public int getMaxHour() {
		return this.maxHour;
	}

	public void setMaxHour(int maxHour) {
		this.maxHour = maxHour;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@PlanningEntityCollectionProperty
	public List<Event> getEvents() {
		return this.events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	@Override
	public HardMediumSoftScore getScore() {
		return this.score;
	}

	@Override
	public void setScore(HardMediumSoftScore score) {
		this.score = score;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<? extends Object> getProblemFacts() {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventSchedule [events=");
		builder.append(this.events);
		builder.append(", score=");
		builder.append(this.score);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Clone will only deep copy the {@link #events}.
	 */
	@Override
	public EventSchedule planningClone() {
		EventSchedule clone = new EventSchedule();
		clone.setPossiblePeriods(possiblePeriods);
		clone.setDaysOfWeek(this.daysOfWeek);
		clone.setYear(this.year);
		clone.setWeekOfYear(this.weekOfYear);
		clone.setMinHour(this.minHour);
		clone.setMaxHour(this.maxHour);
		clone.setUsers(this.users);
		clone.setEvents(this.events.stream().map(event -> event.clone()).collect(Collectors.toList()));
		clone.setScore(this.score);
		return clone;
	}

}
