package hu.smartcampus.appointmentscheduler.domain;

import hu.smartcampus.appointmentscheduler.service.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

/**
 * This class is a {@link PlanningSolution}.
 * 
 * @author adam
 */
@PlanningSolution
public class EventSchedule implements Solution<HardMediumSoftScore>, PlanningCloneable<EventSchedule> {

	/**
	 * The {@link List} of the possible {@link Period}s. See {@link #getPossiblePeriods()}.
	 */
	private List<Period> possiblePeriods;

	/**
	 * The {@link List} of such {@link DayOfWeek}s that must be used during the planning.
	 */
	private List<DayOfWeek> daysOfWeek;

	/**
	 * The year of the {@link EventSchedule}.
	 */
	private int year;

	/**
	 * The number of the week. The planning algorithm works only on those events which {@link Period}s are on this
	 * week.
	 */
	private int weekOfYear;

	/**
	 * The minimum hour on which the algorithm has to work.
	 */
	private int minHour;

	/**
	 * The maxium hour on which the algorithm has to work.
	 */
	private int maxHour;

	/**
	 * Those {@link User}s on who the planning algorithm has to work.
	 */
	private List<User> users;

	/**
	 * The {@link Event}s that has to taken into consideration during planning. This is a
	 * {@link PlanningEntityCollectionProperty}. Please see {@link #getEvents()}.
	 */
	private List<Event> events;

	/**
	 * The score of the {@link EventSchedule}. 0hard/0medium/0soft is the best. Lower values are worse.
	 */
	private HardMediumSoftScore score;

	/**
	 * Constructs an empty {@link EventSchedule}.
	 */
	protected EventSchedule() {
		super();
	}

	/**
	 * Constructs a new {@link EventSchedule}.
	 * 
	 * @param possiblePeriods the possible periods which can be used during planning
	 * @param daysOfWeek the possible days which can be used during planning
	 * @param year the year which must be used during planning
	 * @param weekOfYear the number of the week of the year which must be used during planning
	 * @param minHour the minimum hour of the day which must be used during planning
	 * @param maxHour the maximum hour of the day which must be used during planning
	 * @param users the users which must be used during planning
	 * @param events the events which must be used during planning
	 * @param score the initial score of the {@link EventSchedule}
	 */
	protected EventSchedule(List<Period> possiblePeriods, List<DayOfWeek> daysOfWeek, int year, int weekOfYear,
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

	/**
	 * Returns the possible {@link Period}s of the {@link EventSchedule}. Planning algorithm gets these values before
	 * making a move.
	 * 
	 * @return
	 */
	@ValueRangeProvider(id = "periodRange")
	public List<Period> getPossiblePeriods() {
		return this.possiblePeriods;
	}

	/**
	 * Sets the possible {@link Period}s of the {@link EventSchedule}.
	 * 
	 * @param possiblePeriods the new possible {@link Period}s of the {@link EventSchedule}
	 */
	public void setPossiblePeriods(List<Period> possiblePeriods) {
		this.possiblePeriods = possiblePeriods;
	}

	/**
	 * Returns the {@link DayOfWeek}s of the {@link EventSchedule}.
	 * 
	 * @return the {@link DayOfWeek}s of the {@link EventSchedule}
	 */
	public List<DayOfWeek> getDaysOfWeek() {
		return this.daysOfWeek;
	}

	/**
	 * Sets the {@link DayOfWeek}s of the {@link EventSchedule}.
	 * 
	 * @param daysOfWeek the new {@link DayOfWeek}s of the {@link EventSchedule}.
	 */
	public void setDaysOfWeek(List<DayOfWeek> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	/**
	 * returns the year of the {@link EventSchedule}.
	 * 
	 * @return the year of the {@link EventSchedule}
	 */
	public int getYear() {
		return this.year;
	}

	/**
	 * Sets the year of the {@link EventSchedule}.
	 * 
	 * @param year the new year of the {@link EventSchedule}
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Returns the number of the week of year.
	 * 
	 * @return the number of the week of year
	 */
	public int getWeekOfYear() {
		return this.weekOfYear;
	}

	/**
	 * Sets the number of week of year of the {@link EventSchedule}.
	 * 
	 * @param weekOfYear the new number of week of year of {@link EventSchedule}
	 */
	public void setWeekOfYear(int weekOfYear) {
		this.weekOfYear = weekOfYear;
	}

	/**
	 * Returns the minimum hour of the {@link EventSchedule}.
	 * 
	 * @return the minimum hour of the {@link EventSchedule}
	 */
	public int getMinHour() {
		return this.minHour;
	}

	/**
	 * Sets the minimum hour of the {@link EventSchedule}.
	 * 
	 * @param minHour the new minimum number of the {@link EventSchedule}
	 */
	public void setMinHour(int minHour) {
		this.minHour = minHour;
	}

	/**
	 * Returns the maximum hour of the {@link EventSchedule}.
	 * 
	 * @return the maximum hour of the {@link EventSchedule}
	 */
	public int getMaxHour() {
		return this.maxHour;
	}

	/**
	 * Sets the maximum hour of the {@link EventSchedule}.
	 * 
	 * @param maxHour the new maximum number of the {@link EventSchedule}
	 */
	public void setMaxHour(int maxHour) {
		this.maxHour = maxHour;
	}

	/**
	 * Returns the {@link User}s of the {@link EventSchedule}.
	 * 
	 * @return the {@link User}s of the {@link EventSchedule}
	 */
	public List<User> getUsers() {
		return this.users;
	}

	/**
	 * Sets the {@link User}s of the {@link EventSchedule}.
	 * 
	 * @param users the new {@link User}s of the {@link EventSchedule}
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * Returns the {@link Event}s of the {@link EventSchedule}. This is a {@link PlanningEntityCollectionProperty}
	 * which means that the planning algorithm can change (moves) the instances of this list.
	 * 
	 * @return
	 */
	@PlanningEntityCollectionProperty
	public List<Event> getEvents() {
		return this.events;
	}

	/**
	 * Sets the {@link Event}s of the {@link EventSchedule}.
	 * 
	 * @param events the new {@link Event}s of the {@link EventSchedule}
	 */
	public void setEvents(List<Event> events) {
		this.events = events;
	}

	/**
	 * Returns the {@link Score} of the {@link EventSchedule}.
	 */
	@Override
	public HardMediumSoftScore getScore() {
		return this.score;
	}

	/**
	 * Sets the score of the {@link EventSchedule}.
	 * 
	 * @param score the new score of the {@link EventSchedule}
	 */
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

	/**
	 * Returns the {@link String} representation of the {@link EventSchedule}.
	 */
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
	 * Clone will only deep copy the {@link #events}. This method is only used by the planning algorithm.
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

	/**
	 * Converts {@link EventSchedule} instance to a {@link Schedule} instance.
	 * 
	 * @return a {@link Schedule} instance
	 * @throws ParseException if date conversion fails
	 */
	public Schedule toSchedule() throws ParseException {
		List<Event> lockedEvents = events.stream().filter(event -> event.isLocked()).collect(Collectors.toList());
		List<Event> movableEvents = events.stream().filter(event -> !event.isLocked()).collect(Collectors.toList());

		List<User> unavailableUsers = new ArrayList<>();
		for (Event lockedEvent : lockedEvents) {
			for (Event movableEvent : movableEvents) {
				if (lockedEvent.getPeriod().equals(movableEvent.getPeriod())) {
					for (User user : movableEvent.getUsers()) {
						if (lockedEvent.getUsers().contains(user) && !unavailableUsers.contains(user)) {
							unavailableUsers.add(user);
						}
					}
				}
			}
		}
		Collections.sort(unavailableUsers);

		Schedule schedule = new Schedule();
		schedule.setUnavailableUsers(unavailableUsers.toArray(new User[unavailableUsers.size()]));

		List<User> availableUsers = new ArrayList<>(this.users);
		availableUsers.removeAll(unavailableUsers);
		Collections.sort(availableUsers);
		schedule.setAvailableUsers(availableUsers.toArray(new User[availableUsers.size()]));

		Period period = movableEvents.get(0).getPeriod();
		DayOfWeek dayOfWeek = period.getDay();
		int hour = period.getTimeslot().getHour();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-ww-EEEE-H");
		String dateString = this.year + "-" + this.weekOfYear + "-" + dayOfWeek + "-" + hour;

		Date date = simpleDateFormat.parse(dateString);
		schedule.setDate(date);

		return schedule;
	}

}
