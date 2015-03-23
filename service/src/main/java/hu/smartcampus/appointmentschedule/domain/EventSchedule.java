package hu.smartcampus.appointmentschedule.domain;

import hu.smartcampus.db.model.TEvent;
import hu.smartcampus.db.model.TUser;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.ArrayUtils;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class EventSchedule implements Solution<HardSoftScore> {

	private static final TimeZone budapestTimeZone = TimeZone.getTimeZone("Europe/Budapest");
	private static final SimpleDateFormat dayDateFormat;
	private static final SimpleDateFormat weekDateFormat;
	private static final SimpleDateFormat yearDateFormat;
	private static final SimpleDateFormat hourDateFormat;
	private static final SimpleDateFormat minuteDateFormat;

	private List<User> users;
	private List<Event> events;
	private List<Day> requiredDays;
	private HardSoftScore score;

	static {
		dayDateFormat = new SimpleDateFormat("EEEE");
		dayDateFormat.setTimeZone(budapestTimeZone);

		weekDateFormat = new SimpleDateFormat("w");
		weekDateFormat.setTimeZone(budapestTimeZone);

		yearDateFormat = new SimpleDateFormat("yyyy");
		yearDateFormat.setTimeZone(budapestTimeZone);

		hourDateFormat = new SimpleDateFormat("HH");
		hourDateFormat.setTimeZone(budapestTimeZone);

		minuteDateFormat = new SimpleDateFormat("mm");
		minuteDateFormat.setTimeZone(budapestTimeZone);
	}

	private EventSchedule() {
		super();
	}
	
	private EventSchedule(String[] requiredLoginNames, String[] skippableLoginNames, int year, int weekOfYear, Day[] days) {
		String[] mergedLoginNames = Stream.concat(Arrays.stream(requiredLoginNames), Arrays.stream(skippableLoginNames))
										  .distinct()
										  .toArray(String[]::new);

		List<TUser> queriedTUsers = queryTUsers(mergedLoginNames);
		List<TEvent> everyTEvent = getDistinctTEventsFromTUsers(queriedTUsers);

		this.users = createUsersFromTUsers(queriedTUsers, requiredLoginNames);
		this.requiredDays = Arrays.asList(days);
		this.events = createEventsFromTEvents(everyTEvent, mergedLoginNames, year, weekOfYear, requiredDays);

		// Add events that should be moved by the algorithm
		this.events.add(new Event("Movable event", new Period(Day.Friday, new Timeslot(10)), users, false));
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public HardSoftScore getScore() {
		return this.score;
	}

	@Override
	public void setScore(HardSoftScore score) {
		this.score = score;
	}

	@PlanningEntityCollectionProperty
	public List<Event> getEvents() {
		return this.events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<Day> getRequiredDays() {
		return requiredDays;
	}

	public void setRequiredDays(List<Day> requiredDays) {
		this.requiredDays = requiredDays;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<? extends Object> getProblemFacts() {
		return null;
	}

	@ValueRangeProvider(id = "periodRange")
	public List<? super Period> getPossiblePeriods() {
		return this.requiredDays.stream()
								.flatMap(day -> Timeslot.getPossibleTimeslots().stream().map(timeslot -> new Period(day, timeslot)))
								.distinct()
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

	public static EventSchedule createEventSchedule(String[] requiredLoginNames, String[] skippableLoginNames,
													int year, int weekOfYear, Day[] days) {
		return new EventSchedule(requiredLoginNames, skippableLoginNames, year, weekOfYear, days);
	}

	private List<TUser> queryTUsers(String[] loginNames) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("SMARTCAMPUS");
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		TypedQuery<TUser> query = entityManager.createQuery("SELECT t FROM TUser t WHERE t.loginName IN :loginNames",
				TUser.class);
		query.setParameter("loginNames", Arrays.asList(loginNames));

		List<TUser> result = query.getResultList();

		entityManager.close();
		entityManagerFactory.close();

		return result;
	}

	private List<TEvent> getDistinctTEventsFromTUsers(List<TUser> queriedTUsers) {
		return queriedTUsers.stream()
							.flatMap(tUser -> new ArrayList<>(tUser.getTEvents()).stream())
							.distinct()
							.collect(Collectors.toList());
	}

	private List<User> createUsersFromTUsers(List<TUser> queriedTUsers, String[] requiredLoginNames) {
		return queriedTUsers.stream()
							.map(tUser -> {
								boolean isSkippable = !ArrayUtils.contains(requiredLoginNames, tUser.getLoginName());
								return new User(tUser.getDisplayName(), tUser.getLoginName(), isSkippable);
							})
							.distinct()
							.sorted()
							.collect(Collectors.toList());
	}

	private List<Event> createEventsFromTEvents(List<TEvent> everyTEvent, String[] loginNames, int year,
			int weekOfYear, List<Day> requiredDays) {
		return everyTEvent.stream()
				.filter(tEvent -> Integer.parseInt(yearDateFormat.format(tEvent.getEventStart())) == year)
				.filter(tEvent -> Integer.parseInt(weekDateFormat.format(tEvent.getEventStart())) == weekOfYear)
				.filter(tEvent -> requiredDays.contains(Day.valueOf(dayDateFormat.format(tEvent.getEventStart()))))
				.flatMap(new Function<TEvent, Stream<? extends Event>>() {

					@Override
					public Stream<? extends Event> apply(TEvent tEvent) {
						Timestamp eventStart = tEvent.getEventStart();
						Timestamp eventEnd = tEvent.getEventEnd();

						String title = tEvent.getTitle();
						List<User> usersOfEvent = getRequiredUsersOfEvent(tEvent, loginNames);
						boolean locked = true;

						List<Period> periods = createPeriodsFromTimestamps(eventStart, eventEnd);
						return periods.stream().map(period -> new Event(title, period, usersOfEvent, locked));
					}
				})
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}
	
	private List<User> getRequiredUsersOfEvent(TEvent tEvent, String[] loginNames) {
		return new ArrayList<>(tEvent.getTUsers()).stream()
											   	  .filter(tUser -> ArrayUtils.contains(loginNames, tUser.getLoginName()))
												  .map(tUser -> getUserByLoginName(tUser.getLoginName()))
												  .distinct()
												  .collect(Collectors.toList());
	}

	private User getUserByLoginName(String loginName) {
		Optional<User> result = this.users.stream()
										  .filter(user -> user.getLoginName().equals(loginName))
										  .findFirst();
		return result.isPresent() ? result.get() : null;
	}

	private static List<Period> createPeriodsFromTimestamps(Timestamp eventStart, Timestamp eventEnd) {
		Day day = Day.valueOf(dayDateFormat.format(eventStart));

		int rangeMinValue = Integer.parseInt(hourDateFormat.format(eventStart));
		int rangeMaxValue = Integer.parseInt(hourDateFormat.format(eventEnd));
		int eventEndMinute = Integer.parseInt(minuteDateFormat.format(eventStart));

		if (rangeMinValue == rangeMaxValue || eventEndMinute != 0) {
			rangeMaxValue++;
		}

		return IntStream.range(rangeMinValue, rangeMaxValue)
						.mapToObj(hour -> new Period(day, new Timeslot(hour)))
						.distinct()
						.collect(Collectors.toList());
	}

}
