package hu.smartcampus.appointmentschedule.domain.solver;

import hu.smartcampus.appointmentschedule.domain.EventSchedule;
import hu.smartcampus.appointmentschedule.domain.Period;

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

public class PeriodStrengthWeightFactory implements SelectionSorterWeightFactory<EventSchedule, Period> {

	@Override
	public Comparable<PeriodStrengthWeight> createSorterWeight(EventSchedule solution, Period selectionPeriod) {
		return new PeriodStrengthWeight(selectionPeriod);
	}
	
	public static class PeriodStrengthWeight implements Comparable<PeriodStrengthWeight> {

        private final Period period;

        public PeriodStrengthWeight(Period period) {
            this.period = period;
        }

        @Override
		public int compareTo(PeriodStrengthWeight other) {
        	return Comparator.comparing(Period::getDay).thenComparing(Period::getTimeslot).compare(this.period, other.period);
        }

    }
	
}
