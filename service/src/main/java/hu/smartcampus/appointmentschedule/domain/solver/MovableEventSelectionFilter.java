package hu.smartcampus.appointmentschedule.domain.solver;

import hu.smartcampus.appointmentschedule.domain.Event;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class MovableEventSelectionFilter implements SelectionFilter<Event> {

	@Override
	public boolean accept(ScoreDirector scoreDirector, Event event) {
		return !event.isLocked();
	}

}
