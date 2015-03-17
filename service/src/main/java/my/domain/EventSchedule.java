package my.domain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
	public List<MyPeriod> getPossiblePeriods() {
		final int numOfDays = requiredDays.size();
		final int numOfPossibleTimeslots = MyTimeslot.getPossibleTimeslots().size();

		List<MyPeriod> possiblePeriods = new ArrayList<>(numOfDays * numOfPossibleTimeslots);

		requiredDays.forEach(day -> {
			MyTimeslot.getPossibleTimeslots().forEach(timeslot -> {
				MyPeriod period = new MyPeriod(day, timeslot);
				possiblePeriods.add(period);
			});
		});

		return possiblePeriods;
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

		// TODO trying to add manually some event that conflicts with locked events.
		events.add(new MyEvent("sajat", new MyPeriod(MyDay.Friday, new MyTimeslot(10)), users, false));

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
		Set<TEvent> everyTEvent = new HashSet<>();

		queriedTUsers.forEach(tUser -> everyTEvent.addAll(tUser.getTEvents()));

		return new ArrayList<>(everyTEvent);
	}

	private List<User> createUsersFromTUsers(List<TUser> queriedTUsers, String[] requiredLoginNames) {
		Set<User> users = new HashSet<>(queriedTUsers.size());

		queriedTUsers.forEach(tUser -> {
			boolean isSkippable = !ArrayUtils.contains(requiredLoginNames, tUser.getLoginName());
			users.add(new User(tUser.getDisplayName(), tUser.getLoginName(), isSkippable));
		});

		return new ArrayList<>(users);
	}

	// TODO ez jo lenne? Mi az a flatMap? Ameddig nem jo, addig marad a regi megoldas.
	// SZERINTEM NEM JO, MERT ELTERO A PICKED MOVE. flatmapos cucc monday 16-ot csinal, a sima meg monday 13-mat.
	private List<MyEvent> createEventsFromTEvents(List<TEvent> everyTEvent, String[] loginNames, int year,
			int weekOfYear, List<MyDay> requiredDays) {
//		return everyTEvent.stream()
//				.filter(tEvent -> Integer.parseInt(yearDateFormat.format(tEvent.getEventStart())) == year)
//				.filter(tEvent -> Integer.parseInt(weekDateFormat.format(tEvent.getEventStart())) == weekOfYear)
//				.filter(tEvent -> requiredDays.contains(MyDay.valueOf(dayDateFormat.format(tEvent.getEventStart()))))
//				.distinct()
//				.flatMap(new Function<TEvent, Stream<? extends MyEvent>>() {
//
//					@Override
//					public Stream<? extends MyEvent> apply(TEvent tEvent) {
//						Timestamp eventStart = tEvent.getEventStart();
//						Timestamp eventEnd = tEvent.getEventEnd();
//
//						String title = tEvent.getTitle();
//						List<User> usersOfEvent = getRequiredUsersOfEvent(tEvent, loginNames);
//						boolean locked = true;
//
//						List<MyPeriod> periods = createPeriodsFromTimestamps(eventStart, eventEnd);
//						return periods.stream().map(period -> new MyEvent(title, period, usersOfEvent, locked));
//					}
//				}).collect(Collectors.toList());

		Set<MyEvent> events = new HashSet<>();
		
		everyTEvent.stream().filter(tEvent -> Integer.parseInt(yearDateFormat.format(tEvent.getEventStart())) == year)
		.filter(tEvent -> Integer.parseInt(weekDateFormat.format(tEvent.getEventStart())) == weekOfYear)
		.filter(tEvent -> requiredDays.contains(MyDay.valueOf(dayDateFormat.format(tEvent.getEventStart()))))
		.forEach(tEvent -> {
			Timestamp eventStart = tEvent.getEventStart();
			Timestamp eventEnd = tEvent.getEventEnd();
			
			List<User> usersOfEvent = getRequiredUsersOfEvent(tEvent, loginNames);
			String title = tEvent.getTitle();
			boolean locked = true;
			
			List<MyPeriod> periods = createPeriodsFromTimestamps(eventStart, eventEnd);
			periods.forEach(period -> events.add(new MyEvent(title, period, usersOfEvent, locked)));
		});
		
		return new ArrayList<>(events);
	}
	
	private List<User> getRequiredUsersOfEvent(TEvent tEvent, String[] loginNames) {
		return Arrays.stream(tEvent.getTUsers().toArray(new TUser[0]))
					 .filter(tUser -> ArrayUtils.contains(loginNames, tUser.getLoginName()))
					 .distinct()
					 .map(tUser -> getUserByLoginName(tUser.getLoginName()))
					 .collect(Collectors.toList());
	}

	private User getUserByLoginName(String loginName) {
		Optional<User> result = this.users.stream()
										  .filter(user -> user.getLoginName().equals(loginName))
										  .findFirst();
		return result.isPresent() ? result.get() : null;
	}

	private static List<MyPeriod> createPeriodsFromTimestamps(final Timestamp eventStart, final Timestamp eventEnd) {
		List<MyPeriod> periods = new ArrayList<>();

		MyDay day = MyDay.valueOf(dayDateFormat.format(eventStart));

		int rangeMinValue = Integer.parseInt(hourDateFormat.format(eventStart));
		int rangeMaxValue = Integer.parseInt(hourDateFormat.format(eventEnd));
		int eventEndMinute = Integer.parseInt(minuteDateFormat.format(eventStart));

		if (rangeMinValue == rangeMaxValue || eventEndMinute != 0) {
			rangeMaxValue++;
		}

		IntStream.range(rangeMinValue, rangeMaxValue).forEach(hour -> {
			MyTimeslot timeslot = new MyTimeslot(hour);
			MyPeriod period = new MyPeriod(day, timeslot);
			periods.add(period);
		});

		return periods;
	}

}
