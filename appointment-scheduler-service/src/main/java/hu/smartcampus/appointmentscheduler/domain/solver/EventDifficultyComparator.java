package hu.smartcampus.appointmentscheduler.domain.solver;

import hu.smartcampus.appointmentscheduler.domain.Event;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

public class EventDifficultyComparator implements Comparator<Event> {

	@Override
	public int compare(Event leftEvent, Event rightEvent) {
		return new CompareToBuilder().append(leftEvent.getUsers().size(), rightEvent.getUsers().size()).toComparison();
	}

}