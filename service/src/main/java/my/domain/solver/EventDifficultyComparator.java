package my.domain.solver;

import java.util.Comparator;

import my.domain.MyEvent;

import org.apache.commons.lang.builder.CompareToBuilder;

public class EventDifficultyComparator implements Comparator<MyEvent> {

	@Override
	public int compare(MyEvent leftEvent, MyEvent rightEvent) {
		return new CompareToBuilder().append(leftEvent.getUsers().size(), rightEvent.getUsers().size()).toComparison();
	}

}
