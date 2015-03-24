package hu.smartcampus.appointmentschedule.domain.solver;

import hu.smartcampus.appointmentschedule.domain.Period;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class EventStrengthComparator implements Comparator<Period> {

	@Override
	public int compare(Period leftPeriod, Period rightPeriod) {
		return new CompareToBuilder().append(leftPeriod.getDay(), rightPeriod.getDay())
									 .append(leftPeriod.getTimeslot().getHour(), rightPeriod.getTimeslot().getHour())
									 .toComparison();
	}

}
