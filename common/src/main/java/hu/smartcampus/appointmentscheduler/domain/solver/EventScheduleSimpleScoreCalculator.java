package hu.smartcampus.appointmentscheduler.domain.solver;

import hu.smartcampus.appointmentscheduler.domain.Event;
import hu.smartcampus.appointmentscheduler.domain.EventSchedule;
import hu.smartcampus.appointmentscheduler.domain.User;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class EventScheduleSimpleScoreCalculator implements EasyScoreCalculator<EventSchedule> {

	@Override
	public Score<HardSoftScore> calculateScore(EventSchedule solution) {
		int hardScore = 0;
		int softScore = 0;

		for (Event outerEvent : solution.getEvents()) {
			for (Event innerEvent : solution.getEvents()) {
				if (outerEvent == innerEvent || outerEvent.isLocked() && innerEvent.isLocked()) {
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
