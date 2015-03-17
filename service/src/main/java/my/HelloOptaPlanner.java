package my;

import my.domain.EventSchedule;
import my.domain.MyDay;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloOptaPlanner {

	private static final String SOLVER_CONFIG = "my/eventSolverConfig.xml";
	private static final Logger logger = LoggerFactory.getLogger(HelloOptaPlanner.class);

	public static void main(String[] args) throws InterruptedException {
		logger.info("Hello Opta Planner!");

		SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
		Solver solver = solverFactory.buildSolver();

		// int currentYear = Calendar.getInstance(budapestTimeZone).get(Calendar.YEAR);
		// int currentWeek = Calendar.getInstance(budapestTimeZone).get(Calendar.WEEK_OF_YEAR);
		int currentYear = 2015;
		int currentWeek = 12;
		String[] requiredLoginNames = new String[] { "KOLLARL", "KISSSANDORADAM", "MKOSA" };
		String[] skippableLoginNames = new String[] { "VAGNERA" };
		MyDay[] requiredDays = { MyDay.Monday, MyDay.Tuesday, MyDay.Friday };

		EventSchedule unsolvedEventSchedule = EventSchedule.createEventSchedule(requiredLoginNames,
				skippableLoginNames, currentYear, currentWeek, requiredDays);

		System.out.println("EVENTS");
		unsolvedEventSchedule.getEvents().forEach(System.out::println);
		System.out.println("ENDOFEVENTS");

		System.out.println("USERS");
		unsolvedEventSchedule.getUsers().forEach(System.out::println);
		System.out.println("ENDOFUSERS");

		System.out.println("unsolvedEventSchedule:");
		System.out.println(unsolvedEventSchedule);

		solver.solve(unsolvedEventSchedule);

		EventSchedule solvedEventSchedule = (EventSchedule) solver.getBestSolution();
		System.out.println("solvedEventSchedule:");
		System.out.println(solvedEventSchedule);

		System.out.println("Goodbye Opta Planner!");
	}

}
