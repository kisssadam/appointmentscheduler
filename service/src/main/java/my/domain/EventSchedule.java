package my.domain;

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

import model.TEvent;
import model.TUser;

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
	private List<MyPeriod> periods;
	private List<MyEvent> events;
	private List<MyDay> requiredDays;
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

	public EventSchedule() {
		super();
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
	public List<MyEvent> getEvents() {
		return this.events;
	}

	public void setEvents(List<MyEvent> events) {
		this.events = events;
	}

	public List<MyPeriod> getPeriods() {
		return this.periods;
	}

	public void setPeriods(List<MyPeriod> periods) {
		this.periods = periods;
	}

	public List<MyDay> getRequiredDays() {
		return requiredDays;
	}

	public void setRequiredDays(List<MyDay> requiredDays) {
		this.requiredDays = requiredDays;
	}

	/**
	 * The method is only used if Drools is used for score calculation. Other score directors do not use it. All
	 * planning entities are automatically inserted into the Drools working memory. Do not add them in the method
	 * getProblemFacts().
	 */
	@Override
	public Collection<? extends Object> getProblemFacts() {
		return null;
	}

	@ValueRangeProvider(id = "periodRange")
	public List<? super MyPeriod> getPossiblePeriods() {
		return this.requiredDays.stream()
								.flatMap(day -> MyTimeslot.getPossibleTimeslots().stream().map(timeslot -> new MyPeriod(day, timeslot)))
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
			int year, int weekOfYear, MyDay[] days) {
		EventSchedule eventSchedule = new EventSchedule();

		String[] loginNames = Stream.concat(Arrays.stream(requiredLoginNames), Arrays.stream(skippableLoginNames))
				.toArray(String[]::new);

		List<TUser> queriedTUsers = eventSchedule.queryTUsers(loginNames);
		List<TEvent> everyTEvent = eventSchedule.getDistinctTEventsFromTUsers(queriedTUsers);

		List<User> users = eventSchedule.createUsersFromTUsers(queriedTUsers, requiredLoginNames);
		eventSchedule.setUsers(users);

		List<MyDay> requiredDays = Arrays.asList(days);
		eventSchedule.setRequiredDays(requiredDays);

		List<MyEvent> events = eventSchedule.createEventsFromTEvents(everyTEvent, loginNames, year, weekOfYear,
				requiredDays);
		eventSchedule.setEvents(events);

		// Add events that should be moved by the algorithm
		events.add(new MyEvent("Movable event", new MyPeriod(MyDay.Friday, new MyTimeslot(10)), users, false));

		return eventSchedule;
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

	private List<MyEvent> createEventsFromTEvents(List<TEvent> everyTEvent, String[] loginNames, int year,
			int weekOfYear, List<MyDay> requiredDays) {
		return everyTEvent.stream()
				.filter(tEvent -> Integer.parseInt(yearDateFormat.format(tEvent.getEventStart())) == year)
				.filter(tEvent -> Integer.parseInt(weekDateFormat.format(tEvent.getEventStart())) == weekOfYear)
				.filter(tEvent -> requiredDays.contains(MyDay.valueOf(dayDateFormat.format(tEvent.getEventStart()))))
				.flatMap(new Function<TEvent, Stream<? extends MyEvent>>() {

					@Override
					public Stream<? extends MyEvent> apply(TEvent tEvent) {
						Timestamp eventStart = tEvent.getEventStart();
						Timestamp eventEnd = tEvent.getEventEnd();

						String title = tEvent.getTitle();
						List<User> usersOfEvent = getRequiredUsersOfEvent(tEvent, loginNames);
						boolean locked = true;

						List<MyPeriod> periods = createPeriodsFromTimestamps(eventStart, eventEnd);
						return periods.stream().map(period -> new MyEvent(title, period, usersOfEvent, locked));
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

	private static List<MyPeriod> createPeriodsFromTimestamps(Timestamp eventStart, Timestamp eventEnd) {
		MyDay day = MyDay.valueOf(dayDateFormat.format(eventStart));

		int rangeMinValue = Integer.parseInt(hourDateFormat.format(eventStart));
		int rangeMaxValue = Integer.parseInt(hourDateFormat.format(eventEnd));
		int eventEndMinute = Integer.parseInt(minuteDateFormat.format(eventStart));

		if (rangeMinValue == rangeMaxValue || eventEndMinute != 0) {
			rangeMaxValue++;
		}

		return IntStream.range(rangeMinValue, rangeMaxValue)
						.mapToObj(hour -> new MyPeriod(day, new MyTimeslot(hour)))
						.distinct()
						.collect(Collectors.toList());
	}

}
