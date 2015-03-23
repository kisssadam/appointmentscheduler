package hu.smartcampus.appointmentschedule.domain.solver;

import hu.smartcampus.appointmentschedule.domain.MyEvent;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class MovableEventSelectionFilter implements SelectionFilter<MyEvent> {

	@Override
	public boolean accept(ScoreDirector scoreDirector, MyEvent event) {
		return !event.isLocked();
	}

}
