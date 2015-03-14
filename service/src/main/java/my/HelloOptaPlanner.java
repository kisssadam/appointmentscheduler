package my;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import model.TEvent;
import model.TUser;
import my.domain.EventSchedule;
import my.domain.MyDay;
import my.domain.MyEvent;
import my.domain.MyPeriod;
import my.domain.MyTimeslot;
import my.domain.User;

import org.apache.commons.lang3.ArrayUtils;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloOptaPlanner {

	private static final String SOLVER_CONFIG = "my/eventSolverConfig.xml";
	private static final Logger logger = LoggerFactory.getLogger(HelloOptaPlanner.class);
	private static final TimeZone budapestTimeZone = TimeZone.getTimeZone("Europe/Budapest");
	private static final SimpleDateFormat dayDateFormat;
	private static final SimpleDateFormat weekDateFormat;
	private static final SimpleDateFormat yearDateFormat;
	private static final SimpleDateFormat hourDateFormat;
	private static final SimpleDateFormat minuteDateFormat;
	
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
	
	public static void main(String[] args) throws InterruptedException {
		logger.info("Hello Opta Planner!");
		
		SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
		Solver solver = solverFactory.buildSolver();
		
//		int currentYear = Calendar.getInstance(budapestTimeZone).get(Calendar.YEAR);
//		int currentWeek = Calendar.getInstance(budapestTimeZone).get(Calendar.WEEK_OF_YEAR);
		int currentYear = 2015;
		int currentWeek = 12;
		MyDay[] requiredDays = {MyDay.Monday, MyDay.Tuesday, MyDay.Friday};
		String[] loginNames = new String[] {"KOLLARL", "KISSSANDORADAM", "MKOSA"}; 
		
		EventSchedule unsolvedEventSchedule = createEventSchedule(loginNames, new String[] {"VAGNERA"},currentYear, currentWeek, requiredDays);
//		EventSchedule unsolvedEventSchedule = createEventSchedule(loginNames, currentYear, currentWeek, requiredDays);
//		EventSchedule unsolvedEventSchedule = EventSchedule.createEventSchedule();
		
		System.out.println("EVENTS");
		unsolvedEventSchedule.getEvents().forEach(System.out::println);
		System.out.println("ENDOFEVENTS");
		
		System.out.println("USERS");
		EventSchedule.getUsers().forEach(System.out::println);
		System.out.println("ENDOFUSERS");
		
		System.out.println("unsolvedEventSchedule:");
		System.out.println(unsolvedEventSchedule);
		
		solver.solve(unsolvedEventSchedule);
		
		EventSchedule solvedEventSchedule = (EventSchedule) solver.getBestSolution();
		System.out.println("solvedEventSchedule:");
		System.out.println(solvedEventSchedule);
		
		System.out.println("Goodbye Opta Planner!");
	}
	
	// TODO implement this
	public static EventSchedule createEventSchedule(String[] requiredLoginNames, String[] skippableLoginNames,
													int year, int weekOfYear, MyDay[] days) {
		EventSchedule eventSchedule = new EventSchedule();
		
		String[] loginNames = Stream.concat(Arrays.stream(requiredLoginNames), Arrays.stream(skippableLoginNames))
									.toArray(String[]::new);
		
		List<TUser> queriedTUsers = queryTUsers(loginNames);
		List<TEvent> everyTEvent = getDistinctTEventsFromTUsers(queriedTUsers);
		
		List<User> users = createUsersFromTUsers(queriedTUsers, requiredLoginNames);
		
		List<User> synchronizedEventScheduleUsers = Collections.synchronizedList(EventSchedule.getUsers());
		for (User user : users) {
			synchronized (synchronizedEventScheduleUsers) {
				if (!synchronizedEventScheduleUsers.contains(user)) {
					synchronizedEventScheduleUsers.add(user);
				}
			}
		}
		
		List<MyDay> requiredDays = Arrays.asList(days);
		eventSchedule.setRequiredDays(requiredDays);
		
		List<MyEvent> events = createEventsFromTEvents(everyTEvent, loginNames, year, weekOfYear, requiredDays);
		eventSchedule.setEvents(events);
		
		// TODO trying to add manually some event that conflicts with locked events.
		events.add(new MyEvent("sajat", new MyPeriod(MyDay.Friday, new MyTimeslot(10)), users, false));
		
		return eventSchedule;
	}
	
//	// TODO implement this
//	public static EventSchedule createEventSchedule(String[] loginNames, int year, int weekOfYear, MyDay[] days) {
//		EventSchedule eventSchedule = new EventSchedule();
//		
//		List<TUser> queriedTUsers = queryTUsers(loginNames);
//		List<TEvent> everyTEvent = getDistinctTEventsFromTUsers(queriedTUsers);
//		
//		List<User> users = createUsersFromTUsers(queriedTUsers);
//		
//		List<User> synchronizedEventScheduleUsers = Collections.synchronizedList(EventSchedule.getUsers());
//		for (User user : users) {
//			synchronized (synchronizedEventScheduleUsers) {
//				if (!synchronizedEventScheduleUsers.contains(user)) {
//					synchronizedEventScheduleUsers.add(user);
//				}
//			}
//		}
//		
//		List<MyDay> requiredDays = Arrays.asList(days);
//		eventSchedule.setRequiredDays(requiredDays);
//		
//		List<MyEvent> events = createEventsFromTEvents(everyTEvent, loginNames, year, weekOfYear, requiredDays);
//		eventSchedule.setEvents(events);
//		
//		// TODO trying to add manually some event that conflicts with locked events.
//		events.add(new MyEvent("sajat", new MyPeriod(MyDay.Friday, new MyTimeslot(10)), users, false));
//		
//		return eventSchedule;
//	}
	
	private static List<MyEvent> createEventsFromTEvents(
			List<TEvent> everyTEvent, String[] loginNames, int year, int weekOfYear, List<MyDay> requiredDays) {
		Set<MyEvent> events = new HashSet<>();
		
		everyTEvent.stream()
				   .filter(tEvent -> Integer.parseInt(yearDateFormat.format(tEvent.getEventStart())) == year)
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

	private static List<User> getRequiredUsersOfEvent(TEvent tEvent, String[] loginNames) {
		Set<User> usersOfEvent = new HashSet<>();
		List<String> loginNameList = Arrays.asList(loginNames);
		
		for (TUser tUser : tEvent.getTUsers()) {
			String loginName = tUser.getLoginName();
			if (loginNameList.contains(loginName)) {
				User user = getUserByLoginName(loginName);
				if (user == null) {
					throw new RuntimeException("LoginName: " + loginName + " not found in: " + EventSchedule.getUsers());
				} else {
					usersOfEvent.add(user);
				}
			}
		}
		
		return new ArrayList<>(usersOfEvent);
	}
	
	private static User getUserByLoginName(String loginName) {
		List<User> synchronizedEventScheduleUsers = Collections.synchronizedList(EventSchedule.getUsers());
		
		synchronized (synchronizedEventScheduleUsers) {
			for (User user : synchronizedEventScheduleUsers) {
				if (user.getLoginName().equals(loginName)) {
					return user;
				}
			}
		}
		
		return null;
//		for (User user : EventSchedule.getUsers()) {
//			if (user.getLoginName().equals(loginName)) {
//				return user;
//			}
//		}
//		return null;
	}
	
	private static List<TUser> queryTUsers(String[] loginNames) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("SMARTCAMPUS");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		TypedQuery<TUser> query = entityManager.createQuery("SELECT t FROM TUser t WHERE t.loginName IN :loginNames", TUser.class);
		query.setParameter("loginNames", Arrays.asList(loginNames));
		
		List<TUser> result = query.getResultList();
		
		entityManager.close();
		entityManagerFactory.close();
		
		return result;
	}
	
	private static List<User> createUsersFromTUsers(List<TUser> queriedTUsers, String[] requiredLoginNames) {
		Set<User> users = new HashSet<>(queriedTUsers.size());
		
		queriedTUsers.forEach(tUser -> {
			boolean isSkippable = !ArrayUtils.contains(requiredLoginNames, tUser.getLoginName());
			users.add(new User(tUser.getDisplayName(), tUser.getLoginName(), isSkippable));
		});
		
		return new ArrayList<>(users);
	}

	private static List<TEvent> getDistinctTEventsFromTUsers(List<TUser> queriedTUsers) {
		Set<TEvent> everyTEvent = new HashSet<>();
		
		queriedTUsers.forEach(tUser -> everyTEvent.addAll(tUser.getTEvents()));
		
		return new ArrayList<>(everyTEvent);
	}
	
}
