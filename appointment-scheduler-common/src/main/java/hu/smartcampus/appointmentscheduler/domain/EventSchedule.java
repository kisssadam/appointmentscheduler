package hu.smartcampus.appointmentscheduler.domain;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PlanningSolution
public class EventSchedule implements Solution<HardMediumSoftScore>, PlanningCloneable<EventSchedule> {

	private static final Logger logger = LoggerFactory.getLogger(EventSchedule.class);

	private List<String> requiredLoginNames;
	private List<String> skippableLoginNames;
	private List<String> mergedLoginNames;
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

	public EventSchedule(List<String> requiredLoginNames, List<String> skippableLoginNames,
			List<String> mergedLoginNames, List<DayOfWeek> daysOfWeek, int year, int weekOfYear, int minHour,
			int maxHour, List<User> users, List<Event> events, HardMediumSoftScore score) {
		super();
		this.requiredLoginNames = requiredLoginNames;
		this.skippableLoginNames = skippableLoginNames;
		this.mergedLoginNames = mergedLoginNames;
		this.daysOfWeek = daysOfWeek;
		this.year = year;
		this.weekOfYear = weekOfYear;
		this.minHour = minHour;
		this.maxHour = maxHour;
		this.users = users;
		this.events = events;
		this.score = score;
	}

	public List<String> getRequiredLoginNames() {
		return this.requiredLoginNames;
	}

	public void setRequiredLoginNames(List<String> requiredLoginNames) {
		this.requiredLoginNames = requiredLoginNames;
	}

	public List<String> getSkippableLoginNames() {
		return this.skippableLoginNames;
	}

	public void setSkippableLoginNames(List<String> skippableLoginNames) {
		this.skippableLoginNames = skippableLoginNames;
	}

	public List<String> getMergedLoginNames() {
		return this.mergedLoginNames;
	}

	public void setMergedLoginNames(List<String> mergedLoginNames) {
		this.mergedLoginNames = mergedLoginNames;
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

	@ValueRangeProvider(id = "periodRange")
	public List<Period> getPossiblePeriods() {
		List<Timeslot> possibleTimeslots = getPossibleTimeslots();
		List<Period> possiblePeriods = new ArrayList<>(daysOfWeek.size() * possibleTimeslots.size());

		this.daysOfWeek.forEach(dayOfWeek -> {
			possibleTimeslots.forEach(timeslot -> {
				possiblePeriods.add(new Period(dayOfWeek, timeslot));
			});
		});

		logger.trace("Possible periods are {}.", possiblePeriods);
		return possiblePeriods;
	}

	private List<Timeslot> getPossibleTimeslots() {
		return IntStream.rangeClosed(this.minHour, this.maxHour).mapToObj(hour -> new Timeslot(hour)).distinct()
				.collect(Collectors.toList());
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

		clone.setRequiredLoginNames(this.requiredLoginNames);
		clone.setSkippableLoginNames(this.skippableLoginNames);
		clone.setMergedLoginNames(this.mergedLoginNames);
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
