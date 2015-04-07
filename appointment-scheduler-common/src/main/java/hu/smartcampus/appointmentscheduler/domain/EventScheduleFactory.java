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

/**
 * This Factory is used to construct new {@link EventSchedule} instances.
 * 
 * @author adam
 */
public class EventScheduleFactory {

	/**
	 * The logger of the {@link EventScheduleFactory}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(EventScheduleFactory.class);

	/**
	 * A static reference to the only one instance of {@link EntityManagerFactory}.
	 */
	private static final EntityManagerFactory entityManagerFactory;

	/**
	 * The {@link EntityManager} of {@link EventScheduleFactory}.
	 */
	private EntityManager entityManager;

	static {
		entityManagerFactory = Persistence.createEntityManagerFactory("SMARTCAMPUS");
	}

	/**
	 * Constructs a new {@link EventScheduleFactory}.
	 */
	public EventScheduleFactory() {
		super();
		synchronized (EventScheduleFactory.class) {
			this.entityManager = entityManagerFactory.createEntityManager();
		}
		logger.trace("New EventScheduleFactory has been instantiated.");
	}

	/**
	 * Constructs a new {@link EventSchedule} using the given parameters.
	 * 
	 * @param requiredLoginNames those users login name, who are necessary to attend on the {@link Event}
	 * @param skippableLoginNames those users login name, who are not necessary to attend on the {@link Event}
	 * @param daysOfWeek the possible days of week on which the planning algorithm can work
	 * @param year the year in which the planning algorithm can work
	 * @param weekOfYear the number of the week of the year on which the planning algorithm can work
	 * @param minHour the minimum hour on which the planning algorithm can work
	 * @param maxHour the maximum hour on which the planning algorithm can work
	 * @return the constructed {@link EventSchedule}. Never {@code null}!
	 * @throws IllegalArgumentException if {@code requiredLoginNames}, {@code daysOfWeek} is null or empty or if
	 *             {@code minHour}, {@code maxHour} or {@code weekOfYear} are not in the valid range. For hours the
	 *             valid range is [0, 23], for weekOfYear the valid range is [1, 52].
	 */
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

		logger.trace("Querying TUsers with following login names: {}.", mergedLoginNames);
		List<TUser> queriedTUsers = queryTUsers(mergedLoginNames);
		if (logger.isTraceEnabled()) {
			logger.trace("Queried TUsers are:");
			queriedTUsers.forEach(tUser -> logger.trace("{}", tUser));
		}

		logger.trace("Creating Users from TUsers.");
		List<User> userList = createUsersFromTUsers(queriedTUsers, requiredLoginNameList);
		logger.trace("Users has been created from TUsers.");

		logger.trace("Querying TEvents from TUsers.");
		List<TEvent> everyTEvent = queryTEventsFromTUsers(queriedTUsers);
		if (logger.isTraceEnabled()) {
			logger.trace("The following TEvents has been queried:");
			everyTEvent.forEach(tEvent -> logger.trace("{}", tEvent));
		}

		logger.trace("Creating possible timeslots between {} and {}.", minHour, maxHour);
		List<Timeslot> possibleTimeslots = Timeslot.createPossibleTimeslots(minHour, maxHour);
		if (logger.isTraceEnabled()) {
			logger.trace("Possible timeslots between {} and {} are:", minHour, maxHour);
			possibleTimeslots.forEach(timeslot -> logger.trace("{}", timeslot));
		}
		logger.trace("Creating possible periods from timeslots: {} and days: {}.", possibleTimeslots, dayOfWeekList);
		List<Period> possiblePeriods = Period.createPossiblePeriods(possibleTimeslots, dayOfWeekList);
		if (logger.isTraceEnabled()) {
			logger.trace("Possible periods are:");
			possiblePeriods.forEach(possiblePeriod -> logger.trace("{}", possiblePeriod));
		}

		logger.trace("Creating Events from TEvents.");
		List<Event> eventList = createEventsFromTEvents(everyTEvent, possiblePeriods, year, weekOfYear,
				dayOfWeekList, mergedLoginNames, userList);
		if (logger.isTraceEnabled()) {
			logger.trace("The following events has been created:");
			eventList.forEach(event -> logger.trace("{}", event));
		}

		// Add conflicting event that should be moved by the algorithm
		logger.trace("Adding an unlocked and conflicting event to the previously created event list.");
		DayOfWeek conflictingDay = dayOfWeekList.get(0);
		Timeslot conflictingTimeslot = eventList.isEmpty() ? new Timeslot(minHour) : eventList.get(0).getPeriod()
				.getTimeslot();
		Period conflictingPeriod = new Period(conflictingDay, conflictingTimeslot);
		boolean isLocked = false;
		Event unlockedEvent = new Event("Unlocked event", conflictingPeriod, userList, isLocked);
		eventList.add(unlockedEvent);
		logger.trace("The following unlocked event has been added to the event list: {}", unlockedEvent);

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

	/**
	 * Queries those {@link TUser}s from the database which have the their login names in the give parameter.
	 * 
	 * @param loginNames the login names of the {@link TUser} to query
	 * @return the {@link List} of the queried {@link TUser}s.
	 */
	private List<TUser> queryTUsers(List<String> loginNames) {
		TypedQuery<TUser> query = entityManager.createNamedQuery("TUser.findByLoginName", TUser.class);
		query.setParameter("loginNames", loginNames);

		return query.getResultList();
	}

	/**
	 * Queries the {@link TEvent} from the given {@link TUser}s.
	 * 
	 * @param queriedTUsers the {@link TUser}s to query their {@link TEvent}s
	 * @return the {@link List} of the queried {@link TEvent}s
	 */
	private List<TEvent> queryTEventsFromTUsers(List<TUser> queriedTUsers) {
		return queriedTUsers.stream().flatMap(tUser -> new ArrayList<>(tUser.getTEvents()).stream())
				.collect(Collectors.toList());
	}

	/**
	 * Converts {@link TUser}s to {@link User}s.
	 * 
	 * @param queriedTUsers the {@link TUser} to convert
	 * @param requiredLoginNames if a login name is in this {@link List}, then the {@link User#isSkippable()} must be
	 *            false
	 * @return the {@link List} of the created {@link User}s
	 */
	private List<User> createUsersFromTUsers(List<TUser> queriedTUsers, List<String> requiredLoginNames) {
		return queriedTUsers.stream().map(tUser -> {
			boolean isSkippable = !requiredLoginNames.contains(tUser.getLoginName());
			String displayName = tUser.getDisplayName();
			String loginName = tUser.getLoginName();

			return new User(displayName, loginName, isSkippable);
		}).distinct().sorted().collect(Collectors.toList());
	}

	/**
	 * Converts {@link TEvent}s to {@link Event}s.
	 * 
	 * @param everyTEvent
	 * @param possiblePeriods
	 * @param year
	 * @param weekOfYear
	 * @param daysOfWeek
	 * @param mergedLoginNames
	 * @param users
	 * @return the {@link List} of the created {@link Event}s.
	 */
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

	/**
	 * An event in the database can contain more users then we need to use during planning. This method filters and
	 * returns a {@link List} of {@link User}s which contains only those {@link User}s, which are required during
	 * planning.
	 * 
	 * @param tEvent the entity to filter
	 * @param mergedLoginNames the required login names during planning
	 * @param users every {@link User} that will be used during planning
	 * @return a {@link List} of {@link User}s
	 */
	private List<User> getRequiredUsersOfTEvent(TEvent tEvent, List<String> mergedLoginNames, List<User> users) {
		return new ArrayList<>(tEvent.getTUsers()).stream()
				.filter(tUser -> mergedLoginNames.contains(tUser.getLoginName()))
				.map(tUser -> getUserByLoginName(tUser.getLoginName(), users)).distinct()
				.collect(Collectors.toList());
	}

	/**
	 * Returns that {@link User} from the users parameter which has the loginName login name.
	 * 
	 * @param loginName the login name used during the searching in users
	 * @param users the collection that must be used during searching
	 * @return the {@link User} with the loginName.
	 */
	private User getUserByLoginName(String loginName, List<User> users) {
		return users.stream().filter(user -> user.getLoginName().equals(loginName)).findFirst().orElse(null);
	}

}
