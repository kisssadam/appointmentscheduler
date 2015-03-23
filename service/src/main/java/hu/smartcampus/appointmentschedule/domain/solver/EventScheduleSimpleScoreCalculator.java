package hu.smartcampus.appointmentschedule.domain.solver;

import hu.smartcampus.appointmentschedule.domain.EventSchedule;
import hu.smartcampus.appointmentschedule.domain.MyEvent;
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

		for (MyEvent outerEvent : solution.getEvents()) {
			for (MyEvent innerEvent : solution.getEvents()) {
				if (outerEvent == innerEvent) {
					continue;
				}

				// http://docs.jboss.org/optaplanner/release/6.2.0.CR4/optaplanner-docs/html_single/#immovablePlanningEntities
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
