package my.solver.score;

import java.util.List;

import my.domain.EventSchedule;
import my.domain.MyEvent;
import my.domain.MyPeriod;
import my.domain.MyUnavailablePeriodPenalty;
import my.domain.User;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class EventScheduleSimpleScoreCalculator implements EasyScoreCalculator<EventSchedule> {

	@Override
	public Score<?> calculateScore(EventSchedule solution) {
		int hardScore = 0;
		int softScore = 0;
		
		for (MyEvent outerEvent : solution.getEvents()) {
			for (MyEvent innerEvent : solution.getEvents()) {
				if (outerEvent == innerEvent) {
					continue;
				}
				
				// ettol fuggetlenul, meg tudja mozgatni a lockolt eventet is. Miert??????????
				// http://docs.jboss.org/optaplanner/release/6.2.0.CR4/optaplanner-docs/html_single/#immovablePlanningEntities
				if (outerEvent.isLocked() == innerEvent.isLocked()) {
					continue;
				}
				
				if (outerEvent.getPeriod().equals(innerEvent.getPeriod())) {
					for (User user : innerEvent.getUsers()) {
						if (outerEvent.getUsers().contains(user)) {
							--hardScore;
						}
					}
				}
			}
		}
		
//		List<MyEvent> events = solution.getEvents();
//		for (MyEvent outerEvent : events) {
//			MyPeriod outerPeriod = outerEvent.getPeriod();
//			
//			for (MyEvent innerEvent : events) {
//				if (outerEvent == innerEvent) {
//					continue;
//				} else {
//					MyPeriod innerPeriod = innerEvent.getPeriod();
//					
//					if (outerPeriod.equals(innerPeriod)) {
//						hardScore--;
//					}
//				}
//			}
//		}
		
//		for (MyUnavailablePeriodPenalty myUnavailablePeriodPenalty : solution.getMyUnavailablePeriodPenaltyList()) {
//			if (solution.getPeriods().contains(myUnavailablePeriodPenalty.getPeriod())) {
//				hardScore--;
//			}
//		}
		
		return HardSoftScore.valueOf(hardScore, softScore);
	}
	
}
