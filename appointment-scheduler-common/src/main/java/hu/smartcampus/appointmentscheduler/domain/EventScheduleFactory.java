package hu.smartcampus.appointmentscheduler.domain;

import hu.smartcampus.appointmentscheduler.entity.TEvent;
import hu.smartcampus.appointmentscheduler.entity.TUser;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventScheduleFactory {

	private static final Logger logger = LoggerFactory.getLogger(EventScheduleFactory.class);
	private static final EntityManagerFactory entityManagerFactory;
	private static final ZoneId budapestZoneId;
	private static final DateTimeFormatter dayDateTimeFormatter;
	private static final DateTimeFormatter weekDateTimeFormatter;
	private static final DateTimeFormatter yearDateTimeFormatter;
	private static final DateTimeFormatter hourDateTimeFormatter;
	private static final DateTimeFormatter minuteDateTimeFormatter;

	private EntityManager entityManager;

	static {
		entityManagerFactory = Persistence.createEntityManagerFactory("SMARTCAMPUS");

		budapestZoneId = ZoneId.of("Europe/Budapest");

		dayDateTimeFormatter = DateTimeFormatter.ofPattern("EEEE");
		weekDateTimeFormatter = DateTimeFormatter.ofPattern("w");
		yearDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy");
		hourDateTimeFormatter = DateTimeFormatter.ofPattern("H");
		minuteDateTimeFormatter = DateTimeFormatter.ofPattern("m");
	}

	public EventScheduleFactory() {
		super();
		synchronized (EventScheduleFactory.class) {
			this.entityManager = entityManagerFactory.createEntityManager();
		}
	}

	public EventSchedule newEventSchedule(String[] requiredLoginNames, String[] skippableLoginNames,
			DayOfWeek[] daysOfWeek, int year, int weekOfYear, int minHour, int maxHour) {
		List<String> requiredLoginNameList = Arrays.stream(requiredLoginNames).distinct()
				.collect(Collectors.toList());
		List<String> skippableLoginNameList = Arrays.stream(skippableLoginNames)
				.filter(loginName -> !requiredLoginNameList.contains(loginName)).distinct()
				.collect(Collectors.toList());
		List<String> mergedLoginNames = Stream
				.concat(requiredLoginNameList.stream(), skippableLoginNameList.stream()).distinct()
				.collect(Collectors.toList());

		Arrays.sort(daysOfWeek);
		List<DayOfWeek> dayOfWeekList = Arrays.asList(daysOfWeek);

		List<TUser> queriedTUsers = queryTUsers(mergedLoginNames);
		List<User> userList = createUsersFromTUsers(queriedTUsers, requiredLoginNameList);

		List<TEvent> everyTEvent = queryTEventsFromTUsers(queriedTUsers);
		List<Period> possiblePeriods = createPossiblePeriods(getPossibleTimeslots(minHour, maxHour), dayOfWeekList);
		List<Event> eventList = createEventsFromTEvents(everyTEvent, possiblePeriods, year, weekOfYear,
				dayOfWeekList, mergedLoginNames, userList);

		// Add conflicting event that should be moved by the algorithm
		DayOfWeek conflictingDay = dayOfWeekList.isEmpty() ? DayOfWeek.MONDAY : dayOfWeekList.get(0);
		Timeslot conflictingTimeslot = eventList.isEmpty() ? new Timeslot(minHour) : eventList.get(0).getPeriod()
				.getTimeslot();
		Period conflictingPeriod = new Period(conflictingDay, conflictingTimeslot);
		boolean isLocked = false;

		eventList.add(new Event("Movable event", conflictingPeriod, userList, isLocked));

		EventSchedule eventSchedule = new EventSchedule();
		eventSchedule.setPossiblePeriods(possiblePeriods);
		eventSchedule.setDaysOfWeek(dayOfWeekList);
		eventSchedule.setYear(year);
		eventSchedule.setWeekOfYear(weekOfYear);
		eventSchedule.setMinHour(minHour);
		eventSchedule.setMaxHour(maxHour);
		eventSchedule.setUsers(userList);
		eventSchedule.setEvents(eventList);
		return eventSchedule;
	}

	private List<Timeslot> getPossibleTimeslots(int minHour, int maxHour) {
		return IntStream.rangeClosed(minHour, maxHour).mapToObj(hour -> new Timeslot(hour)).distinct()
				.collect(Collectors.toList());
	}

	private List<Period> createPossiblePeriods(List<Timeslot> timeslots, List<DayOfWeek> daysOfWeek) {
		List<Period> possiblePeriods = new ArrayList<>(timeslots.size() * daysOfWeek.size());

		daysOfWeek.forEach(dayOfWeek -> {
			timeslots.forEach(timeslot -> {
				possiblePeriods.add(new Period(dayOfWeek, timeslot));
			});
		});

		return possiblePeriods;
	}

	private List<TUser> queryTUsers(List<String> loginNames) {
		TypedQuery<TUser> query = entityManager.createNamedQuery("TUser.findByLoginName", TUser.class);
		query.setParameter("loginNames", loginNames);

		return query.getResultList();
	}

	private List<TEvent> queryTEventsFromTUsers(List<TUser> queriedTUsers) {
		return queriedTUsers.stream().flatMap(tUser -> new ArrayList<>(tUser.getTEvents()).stream()).distinct()
				.collect(Collectors.toList());
	}

	private List<User> createUsersFromTUsers(List<TUser> queriedTUsers, List<String> requiredLoginNames) {
		return queriedTUsers.stream().map(tUser -> {
			boolean isSkippable = !requiredLoginNames.contains(tUser.getLoginName());
			String displayName = tUser.getDisplayName();
			String loginName = tUser.getLoginName();

			return new User(displayName, loginName, isSkippable);
		}).distinct().sorted().collect(Collectors.toList());
	}

	private List<Event> createEventsFromTEvents(List<TEvent> everyTEvent, List<Period> possiblePeriods, int year,
			int weekOfYear, List<DayOfWeek> daysOfWeek, List<String> mergedLoginNames, List<User> users) {
		logger.trace("Queried TEvents are: {}.", everyTEvent);
		List<Event> result = everyTEvent
				.stream()
				.filter(tEvent -> Integer.parseInt(tEvent.getEventStart().toInstant().atZone(budapestZoneId)
						.format(yearDateTimeFormatter)) == year)
				.filter(tEvent -> Integer.parseInt(tEvent.getEventStart().toInstant().atZone(budapestZoneId)
						.format(weekDateTimeFormatter)) == weekOfYear)
				.filter(tEvent -> daysOfWeek.contains(DayOfWeek.valueOf(tEvent.getEventStart().toInstant()
						.atZone(budapestZoneId).format(dayDateTimeFormatter).toUpperCase())))
				.flatMap(new Function<TEvent, Stream<? extends Event>>() {

					@Override
					public Stream<? extends Event> apply(TEvent tEvent) {
						Timestamp eventStart = tEvent.getEventStart();
						Timestamp eventEnd = tEvent.getEventEnd();

						String title = tEvent.getTitle();
						List<User> usersOfEvent = getRequiredUsersOfTEvent(tEvent, mergedLoginNames, users);
						boolean locked = true;

						List<Period> periods = createPeriodsFromTimestamps(eventStart, eventEnd);
						return periods.stream().map(period -> new Event(title, period, usersOfEvent, locked));
					}
				}).filter(event -> possiblePeriods.contains(event.getPeriod())).distinct().sorted()
				.collect(Collectors.toList());
		logger.trace("Created Events from TEvents are: {}", result);
		return result;
	}

	private List<User> getRequiredUsersOfTEvent(TEvent tEvent, List<String> mergedLoginNames, List<User> users) {
		return new ArrayList<>(tEvent.getTUsers()).stream()
				.filter(tUser -> mergedLoginNames.contains(tUser.getLoginName()))
				.map(tUser -> getUserByLoginName(tUser.getLoginName(), users)).distinct()
				.collect(Collectors.toList());
	}

	private User getUserByLoginName(String loginName, List<User> users) {
		Optional<User> result = users.stream().filter(user -> user.getLoginName().equals(loginName)).findFirst();
		return result.isPresent() ? result.get() : null;
	}

	private List<Period> createPeriodsFromTimestamps(Timestamp eventStart, Timestamp eventEnd) {
		DayOfWeek dayOfWeek = DayOfWeek.valueOf(eventStart.toInstant().atZone(budapestZoneId)
				.format(dayDateTimeFormatter).toUpperCase());

		int rangeMinValue = Integer.parseInt(eventStart.toInstant().atZone(budapestZoneId)
				.format(hourDateTimeFormatter));
		int rangeMaxValue = Integer.parseInt(eventEnd.toInstant().atZone(budapestZoneId)
				.format(hourDateTimeFormatter));

		int eventEndMinute = Integer.parseInt(eventStart.toInstant().atZone(budapestZoneId)
				.format(minuteDateTimeFormatter));

		if (rangeMinValue == rangeMaxValue || eventEndMinute != 0) {
			rangeMaxValue++;
		}

		return IntStream.range(rangeMinValue, rangeMaxValue)
				.mapToObj(hour -> new Period(dayOfWeek, new Timeslot(hour))).distinct().collect(Collectors.toList());
	}

}
