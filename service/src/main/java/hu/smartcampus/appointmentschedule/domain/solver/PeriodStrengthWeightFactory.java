package hu.smartcampus.appointmentschedule.domain.solver;

import hu.smartcampus.appointmentschedule.domain.EventSchedule;
import hu.smartcampus.appointmentschedule.domain.Period;

import org.apache.commons.lang3.builder.CompareToBuilder;
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
            return new CompareToBuilder()
                    .append(period.getDay(), other.period.getDay())
                    .append(period.getTimeslot().getHour(), other.period.getTimeslot().getHour())
                    .toComparison();
        }

    }
	
}
