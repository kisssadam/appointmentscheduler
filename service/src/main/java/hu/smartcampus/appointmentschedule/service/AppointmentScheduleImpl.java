package hu.smartcampus.appointmentschedule.service;

import hu.smartcampus.appointmentschedule.domain.Day;
import hu.smartcampus.appointmentschedule.domain.Event;
import hu.smartcampus.appointmentschedule.domain.EventSchedule;
import hu.smartcampus.appointmentschedule.domain.Period;

import java.util.Optional;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

@WebService(endpointInterface = "hu.smartcampus.appointmentschedule.service.AppointmentSchedule")
@SOAPBinding(style = Style.RPC)
public class AppointmentScheduleImpl implements AppointmentSchedule {

	private static final String SOLVER_CONFIG = "hu/smartcampus/appointmentschedule/solver/eventScheduleSolverConfig.xml";

	@Override
	public Period getBestPeriod(String[] requiredLoginNames, String[] skippableLoginNames, int year, int weekOfYear, Day[] days) {
		SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
		Solver solver = solverFactory.buildSolver();
		
		EventSchedule unsolvedEventSchedule = EventSchedule.createEventSchedule(requiredLoginNames, skippableLoginNames, year, weekOfYear, days);
		solver.solve(unsolvedEventSchedule);
		
		EventSchedule solvedEventSchedule = (EventSchedule) solver.getBestSolution();
		
		Optional<Event> resultEvent = solvedEventSchedule.getEvents().stream().filter(event -> !event.isLocked()).findFirst();
		return resultEvent.isPresent() ? resultEvent.get().getPeriod() : null;
	}

}
