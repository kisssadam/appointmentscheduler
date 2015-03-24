package hu.smartcampus.appointmentschedule.domain.solver;

import hu.smartcampus.appointmentschedule.domain.Period;

import java.util.Comparator;

public class EventStrengthComparator implements Comparator<Period> {

	@Override
	public int compare(Period leftPeriod, Period rightPeriod) {
		return Comparator.comparing(Period::getDay).thenComparing(Period::getTimeslot).compare(leftPeriod, rightPeriod);
	}

}
