package my.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.persistence.xstream.impl.score.XStreamScoreConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@PlanningSolution
@XStreamAlias("EventSchedule")
public class EventSchedule implements Solution<HardSoftScore> {
	
	private static final Logger logger = LoggerFactory.getLogger(EventSchedule.class);
	
	private static List<User> users;	// ne legyen static!
	private List<MyPeriod> periods;
	private List<MyEvent> events;
	private List<MyDay> requiredDays;
	
	@ValueRangeProvider(id = "periodRange")
	public List<MyPeriod> getPossiblePeriods() {
		final int numOfDays = requiredDays.size();
		final int numOfPossibleTimeslots = MyTimeslot.getPossibleTimeslots().size();
		
		List<MyPeriod> possiblePeriods = new ArrayList<>(numOfDays*numOfPossibleTimeslots);
		
		for (MyDay currentDay : requiredDays) {
			for (MyTimeslot timeslot: MyTimeslot.getPossibleTimeslots()) {
				MyPeriod period = new MyPeriod(currentDay, timeslot);
				possiblePeriods.add(period);
			}
		}
		
		return possiblePeriods;
	}
	
	@XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftScoreDefinition.class})
	private HardSoftScore score;
	
	static {
		users = new ArrayList<>();
	}
	
	public static List<User> getUsers() {
		return EventSchedule.users;
	}

//	public static void setUsers(Set<User> users) {
//		EventSchedule.users = users;
//	}
	
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
	 * The method is only used if Drools is used for score calculation.
	 * Other score directors do not use it.
	 * All planning entities are automatically inserted into the Drools working memory.
	 * Do not add them in the method getProblemFacts().
	 */
	@Override
	public Collection<? extends Object> getProblemFacts() {
		return null;
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

	public static EventSchedule createEventSchedule() {
		EventSchedule eventSchedule = new EventSchedule();
		
//		EventSchedule.users = new ArrayList<>();
		eventSchedule.periods = new ArrayList<>();		
		eventSchedule.events = new ArrayList<>();
		
		// creating users
		EventSchedule.users.add(new User("Adam", "kisssandoradam"));
		EventSchedule.users.add(new User("Peter", "kisspeti2000"));
		EventSchedule.users.add(new User("David", "apagyidavid"));
		
		// adding non movable events
		for (int hour = MyTimeslot.getMinHour()+1; hour < MyTimeslot.getMaxHour(); hour++) {
			for (MyDay day : MyDay.values()) {
				if (day == MyDay.Tuesday) {
					continue;
				}
				MyPeriod period = new MyPeriod(day, new MyTimeslot(hour));
				eventSchedule.periods.add(period);
				eventSchedule.events.add(new MyEvent(hour + ". event", period, new ArrayList<>(EventSchedule.users), true));			
			}
		}
		
		// adding movable events
		eventSchedule.events.add(new MyEvent("mozgathato event", new MyPeriod(MyDay.Monday, new MyTimeslot(9)), new ArrayList<>(EventSchedule.users), false));
		eventSchedule.events.add(new MyEvent("mozgathato event", new MyPeriod(MyDay.Monday, new MyTimeslot(10)), new ArrayList<>(EventSchedule.users), false));
		eventSchedule.events.add(new MyEvent("mozgathato event", new MyPeriod(MyDay.Monday, new MyTimeslot(11)), new ArrayList<>(EventSchedule.users), false));
		
		return eventSchedule;
	}
	
}
