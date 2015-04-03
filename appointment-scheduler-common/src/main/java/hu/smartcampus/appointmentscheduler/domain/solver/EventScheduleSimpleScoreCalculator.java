package hu.smartcampus.appointmentscheduler.domain.solver;

import hu.smartcampus.appointmentscheduler.domain.Event;
import hu.smartcampus.appointmentscheduler.domain.EventSchedule;
import hu.smartcampus.appointmentscheduler.domain.User;

import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class EventScheduleSimpleScoreCalculator implements EasyScoreCalculator<EventSchedule> {

	@Override
	public Score<HardMediumSoftScore> calculateScore(EventSchedule solution) {
		int hardScore = 0;
		int mediumScore = 0;
		int softScore = 0;

		List<Event> lockedEvents = solution.getEvents().stream().filter(event -> event.isLocked()).collect(Collectors.toList());
		List<Event> movableEvents = solution.getEvents().stream().filter(event -> !event.isLocked()).collect(Collectors.toList());
		
		for (Event lockedEvent : lockedEvents) {
			for (Event movableEvent : movableEvents) {
				if (lockedEvent.getPeriod().equals(movableEvent.getPeriod())) {
					for (User user : movableEvent.getUsers()) {
						if (lockedEvent.getUsers().contains(user)) {
							if (user.isSkippable()) {
								--mediumScore;
							} else {
								--hardScore;
							}
						}
					}
				}
			}
		}
		
		for (Event movableEvent : movableEvents) {
			softScore -= movableEvent.getPeriod().getTimeslot().getHour() - solution.getMinHour();
		}
		
		return HardMediumSoftScore.valueOf(hardScore, mediumScore, softScore);
	}

}
