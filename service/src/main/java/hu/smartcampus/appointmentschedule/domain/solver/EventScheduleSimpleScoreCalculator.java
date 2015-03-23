package hu.smartcampus.appointmentschedule.domain.solver;

import hu.smartcampus.appointmentschedule.domain.EventSchedule;
import hu.smartcampus.appointmentschedule.domain.Event;
import hu.smartcampus.appointmentschedule.domain.User;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

// TODO ezt erosen tuningolni kell ha megvannak a megfelelo parameterek.
public class EventScheduleSimpleScoreCalculator implements EasyScoreCalculator<EventSchedule> {

	@Override
	public Score<HardSoftScore> calculateScore(EventSchedule solution) {
		int hardScore = 0;
		int softScore = 0;

		for (Event outerEvent : solution.getEvents()) {
			for (Event innerEvent : solution.getEvents()) {
				if (outerEvent == innerEvent) {
					continue;
				}

				// http://docs.jboss.org/optaplanner/release/6.2.0.CR4/optaplanner-docs/html_single/#immovablePlanningEntities
				// TODO ez itt jo? nem kellene ez? outerEvent.isLocked() == true && innerEvent.isLocked() == true // Mi a kulonbseg?
				if (outerEvent.isLocked() == innerEvent.isLocked()) {
					continue;
				}

				if (outerEvent.getPeriod().equals(innerEvent.getPeriod())) {
					for (User user : innerEvent.getUsers()) {
						if (outerEvent.getUsers().contains(user)) {
							if (user.isSkippable()) {
								--softScore;
							} else {
								--hardScore;
							}
						}
					}
				}
			}
		}

		return HardSoftScore.valueOf(hardScore, softScore);
	}

}
