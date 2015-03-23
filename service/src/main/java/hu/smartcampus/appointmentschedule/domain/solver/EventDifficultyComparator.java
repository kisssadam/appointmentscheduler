package hu.smartcampus.appointmentschedule.domain.solver;

import hu.smartcampus.appointmentschedule.domain.MyEvent;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

public class EventDifficultyComparator implements Comparator<MyEvent> {

	@Override
	public int compare(MyEvent leftEvent, MyEvent rightEvent) {
		return new CompareToBuilder().append(leftEvent.getUsers().size(), rightEvent.getUsers().size()).toComparison();
	}

}
