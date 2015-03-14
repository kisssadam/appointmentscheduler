package my;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.IntStream;

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
		System.out.println("Hello Opta Planner!");
		
		SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
		Solver solver = solverFactory.buildSolver();
		
		/*
		 * SELECT * FROM SMARTCAMPUS.T_USER WHERE DISPLAY_NAME LIKE '%Ádám%';
		 */
//		EventSchedule unsolvedEventSchedule = EventSchedule.createEventSchedule(new String[] {"KOLLARL", "KISSSANDORADAM", "MKOSA", "VAGNERA"});
		
//		int currentYear = Calendar.getInstance(budapestTimeZone).get(Calendar.YEAR);
//		int currentWeek = Calendar.getInstance(budapestTimeZone).get(Calendar.WEEK_OF_YEAR);
		int currentYear = 2015;
		int currentWeek = 12;
		MyDay[] requiredDays = {MyDay.Monday, MyDay.Tuesday};
		
		EventSchedule unsolvedEventSchedule = createEventSchedule(
				new String[] {"KOLLARL", "KISSSANDORADAM", "MKOSA", "VAGNERA"},
				currentYear,
				currentWeek,
				requiredDays);
		
		System.out.println("EVENTS");
		for (MyEvent event : unsolvedEventSchedule.getEvents()) {
			System.out.println(event);
		}
		System.out.println("ENDOFEVENTS");
		
		System.out.println("USERS");
		for (User user : EventSchedule.getUsers()) {
			System.out.println(user);
		}
		System.out.println("ENDOFUSERS");
		
		System.out.println(unsolvedEventSchedule);
		
//		EventSchedule unsolvedEventSchedule = EventSchedule.createEventSchedule();
		
//		new Thread(() -> solver.solve(unsolvedEventSchedule)).start();
//		Thread.sleep(5000);

		solver.solve(unsolvedEventSchedule);
		
		EventSchedule solvedEventSchedule = (EventSchedule) solver.getBestSolution();
		System.out.println("solvedEventSchedule:");
		System.out.println(solvedEventSchedule);
		
		System.out.println("Goodbye Opta Planner!");
	}
	
	// TODO implement this
	public static EventSchedule createEventSchedule(String[] loginNames, int year, int weekOfYear, MyDay[] days) {
		EventSchedule eventSchedule = new EventSchedule();
		
		List<TUser> queriedTUsers = queryTUsers(loginNames);
		List<TEvent> everyTEvent = getDistinctTEventsFromTUsers(queriedTUsers);
		
		List<User> users = createUsersFromTUsers(queriedTUsers);
		EventSchedule.getUsers().addAll(users);
		
		List<MyDay> requiredDays = Arrays.asList(days);
		eventSchedule.setRequiredDays(requiredDays);
		
		List<MyEvent> events = createEventsFromTEvents(everyTEvent, loginNames, year, weekOfYear, requiredDays);
		eventSchedule.setEvents(events);
		
		// TODO trying to add manually some event that conflicts with locked events.
		events.add(new MyEvent("sajat", new MyPeriod(MyDay.Friday, new MyTimeslot(10)), users, false));
		
		return eventSchedule;
	}
	
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
					   
					   System.out.println("WEEK: " + Integer.parseInt(weekDateFormat.format(eventStart)));
					   System.out.println("YEAR: " + Integer.parseInt(yearDateFormat.format(eventStart)));
					
					   List<User> usersOfEvent = getRequiredUsersOfEvent(tEvent, loginNames);
					
					   String title = tEvent.getTitle();
					   boolean locked = true;
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
						   
						   events.add(new MyEvent(title, period, usersOfEvent, locked));
					   });
		});
		
		return new ArrayList<>(events);
	}

	private static User getUserByLoginName(String loginName) {
		for (User user : EventSchedule.getUsers()) {
			if (user.getLoginName().equals(loginName)) {
				return user;
			}
		}
		return null;
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
	
	private static List<User> createUsersFromTUsers(List<TUser> queriedTUsers) {
		Set<User> users = new HashSet<>(queriedTUsers.size());
		
		queriedTUsers.forEach(tUser -> users.add(new User(tUser.getDisplayName(), tUser.getLoginName())));
		
		return new ArrayList<>(users);
	}

	private static List<TEvent> getDistinctTEventsFromTUsers(List<TUser> queriedTUsers) {
		Set<TEvent> everyTEvent = new HashSet<>();
		
		queriedTUsers.forEach(tUser -> {
			tUser.getTEvents().size();	// prefetch
			everyTEvent.addAll(tUser.getTEvents());
		});
		
		return new ArrayList<>(everyTEvent);
	}
	
}
