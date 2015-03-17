package my.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class EventSchedule implements Solution<HardSoftScore> {
	
	private static List<User> users;
	private List<MyPeriod> periods;
	private List<MyEvent> events;
	private List<MyDay> requiredDays;
	private HardSoftScore score;
	
	static {
		users = new ArrayList<>();
	}
	
	public static List<User> getUsers() {
		return EventSchedule.users;
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
	 * The method is only used if Drools is used for score calculation.
	 * Other score directors do not use it.
	 * All planning entities are automatically inserted into the Drools working memory.
	 * Do not add them in the method getProblemFacts().
	 */
	@Override
	public Collection<? extends Object> getProblemFacts() {
		return null;
	}

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
	
}
