package hu.smartcampus.appointmentscheduler.domain.solver;

import hu.smartcampus.appointmentscheduler.domain.Period;

import java.util.Comparator;

public class EventStrengthComparator implements Comparator<Period> {

	@Override
	public int compare(Period leftPeriod, Period rightPeriod) {
		return Comparator.comparing(Period::getDay).thenComparing(Period::getTimeslot).compare(leftPeriod, rightPeriod);
	}

}
