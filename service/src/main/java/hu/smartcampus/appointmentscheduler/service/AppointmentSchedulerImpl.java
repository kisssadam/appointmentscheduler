package hu.smartcampus.appointmentscheduler.service;

import hu.smartcampus.appointmentscheduler.domain.Event;
import hu.smartcampus.appointmentscheduler.domain.EventSchedule;
import hu.smartcampus.appointmentscheduler.domain.Period;
import hu.smartcampus.appointmentscheduler.service.AppointmentScheduler;

import java.time.DayOfWeek;
import java.util.Optional;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

@WebService(endpointInterface = "hu.smartcampus.appointmentscheduler.service.AppointmentScheduler")
@SOAPBinding(style = Style.RPC)
public class AppointmentSchedulerImpl implements AppointmentScheduler {

	private static final String SOLVER_CONFIG = "hu/smartcampus/appointmentscheduler/solver/eventScheduleSolverConfig.xml";

	@Override
	public Period getBestPeriod(String[] requiredLoginNames, String[] skippableLoginNames, int year, int weekOfYear, DayOfWeek[] daysOfWeek) {
		SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
		Solver solver = solverFactory.buildSolver();
		
		EventSchedule unsolvedEventSchedule = EventSchedule.createEventSchedule(requiredLoginNames, skippableLoginNames, year, weekOfYear, daysOfWeek);
		solver.solve(unsolvedEventSchedule);
		
		EventSchedule solvedEventSchedule = (EventSchedule) solver.getBestSolution();
		
		Optional<Event> resultEvent = solvedEventSchedule.getEvents().stream().filter(event -> !event.isLocked()).findFirst();
		return resultEvent.isPresent() ? resultEvent.get().getPeriod() : null;
	}

}