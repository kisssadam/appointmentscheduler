package hu.smartcampus.appointmentscheduler.domain;

import hu.smartcampus.appointmentscheduler.entity.TEvent;
import hu.smartcampus.appointmentscheduler.entity.TUser;
import hu.smartcampus.appointmentscheduler.utils.FormatUtils;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
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

	private EntityManager entityManager;

	static {
		entityManagerFactory = Persistence.createEntityManagerFactory("SMARTCAMPUS");
	}

	public EventScheduleFactory() {
		super();
		synchronized (EventScheduleFactory.class) {
			this.entityManager = entityManagerFactory.createEntityManager();
		}
		logger.trace("New EventScheduleFactory has been instantiated.");
	}

	public EventSchedule newEventSchedule(String[] requiredLoginNames, String[] skippableLoginNames,
			DayOfWeek[] daysOfWeek, int year, int weekOfYear, int minHour, int maxHour) {
		logger.trace("Checking arguments in newEventSchedule().");
		if (requiredLoginNames == null || requiredLoginNames.length == 0) {
			throw new IllegalArgumentException("Illegal requiredLoginNames argument: "
					+ Arrays.toString(requiredLoginNames) + ".");
		}
		if (daysOfWeek == null || daysOfWeek.length == 0) {
			throw new IllegalArgumentException("Illegal daysOfWeek argument: " + Arrays.toString(daysOfWeek) + ".");
		}
		if (weekOfYear < 1 || weekOfYear > 52) {
			String message = "Argument weekOfYear is " + weekOfYear + ". It should be between 1 and 52.";
			throw new IllegalArgumentException(message);
		}
		if (minHour < 0 || minHour > 23) {
			String message = "Argument minHour is " + minHour + ". It should be between 0 and 23.";
			throw new IllegalArgumentException(message);
		}
		if (maxHour < 0 || maxHour > 23) {
			String message = "Argument maxHour is " + maxHour + ". It should be between 0 and 23.";
			throw new IllegalArgumentException(message);
		}
		if (minHour > maxHour) {
			String message = "Argument minHour (" + minHour + ") is bigger than maxHour (" + maxHour + ")";
			throw new IllegalArgumentException(message);
		}
		logger.trace("Finished checking arguments in newEventSchedule(). Creating EventSchedule.");

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
		List<Timeslot> possibleTimeslots = Timeslot.createPossibleTimeslots(minHour, maxHour);
		List<Period> possiblePeriods = Period.createPossiblePeriods(possibleTimeslots, dayOfWeekList);
		List<Event> eventList = createEventsFromTEvents(everyTEvent, possiblePeriods, year, weekOfYear,
				dayOfWeekList, mergedLoginNames, userList);

		// Add conflicting event that should be moved by the algorithm
		DayOfWeek conflictingDay = dayOfWeekList.get(0);
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

	private List<TUser> queryTUsers(List<String> loginNames) {
		TypedQuery<TUser> query = entityManager.createNamedQuery("TUser.findByLoginName", TUser.class);
		query.setParameter("loginNames", loginNames);

		return query.getResultList();
	}

	private List<TEvent> queryTEventsFromTUsers(List<TUser> queriedTUsers) {
		return queriedTUsers.stream().flatMap(tUser -> new ArrayList<>(tUser.getTEvents()).stream())
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
		return everyTEvent.stream()
				.filter(tEvent -> FormatUtils.getYearFromTimestamp(tEvent.getEventStart()) == year)
				.filter(tEvent -> FormatUtils.getWeekFromTimestamp(tEvent.getEventStart()) == weekOfYear)
				.filter(tEvent -> daysOfWeek.contains(FormatUtils.getDayOfWeekFromTimeStamp(tEvent.getEventStart())))
				.flatMap(new Function<TEvent, Stream<? extends Event>>() {
					@Override
					public Stream<? extends Event> apply(TEvent tEvent) {
						Timestamp eventStart = tEvent.getEventStart();
						Timestamp eventEnd = tEvent.getEventEnd();

						String title = tEvent.getTitle();
						List<User> usersOfEvent = getRequiredUsersOfTEvent(tEvent, mergedLoginNames, users);
						boolean locked = true;

						List<Period> periods = Period.createPeriodsFromTimestamps(eventStart, eventEnd);
						return periods.stream().map(period -> new Event(title, period, usersOfEvent, locked));
					}
				}).filter(event -> possiblePeriods.contains(event.getPeriod())).distinct().sorted()
				.collect(Collectors.toList());
	}

	private List<User> getRequiredUsersOfTEvent(TEvent tEvent, List<String> mergedLoginNames, List<User> users) {
		return new ArrayList<>(tEvent.getTUsers()).stream()
				.filter(tUser -> mergedLoginNames.contains(tUser.getLoginName()))
				.map(tUser -> getUserByLoginName(tUser.getLoginName(), users)).distinct()
				.collect(Collectors.toList());
	}

	private User getUserByLoginName(String loginName, List<User> users) {
		return users.stream().filter(user -> user.getLoginName().equals(loginName)).findFirst().orElse(null);
	}

}
