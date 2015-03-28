package hu.smartcampus.appointmentscheduler.service;

import hu.smartcampus.appointmentscheduler.domain.Event;
import hu.smartcampus.appointmentscheduler.domain.EventSchedule;
import hu.smartcampus.appointmentscheduler.domain.Period;
import hu.smartcampus.appointmentscheduler.domain.User;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(endpointInterface = "hu.smartcampus.appointmentscheduler.service.AppointmentScheduler")
@SOAPBinding(style = Style.RPC)
public class AppointmentSchedulerImpl implements AppointmentScheduler {

	private static final String SOLVER_CONFIG = "hu/smartcampus/appointmentscheduler/solver/eventScheduleSolverConfig.xml";
	private static final Logger logger = LoggerFactory.getLogger(AppointmentSchedulerImpl.class);
	
	private static Map<UUID, EventSchedule> unsolvedEventSchedules;
	private static Map<UUID, Solver> solvers;
	
	static {
		unsolvedEventSchedules = new ConcurrentHashMap<>();
		solvers = new ConcurrentHashMap<>();
	}
	
	@Override
	public UUID createAppointmentScheduler(String[] requiredLoginNames, String[] skippableLoginNames, DayOfWeek[] daysOfWeek, int year, int weekOfYear, int minHour, int maxHour) {
		UUID requestId = UUID.randomUUID();
		EventSchedule unsolvedEventSchedule = EventSchedule.createEventSchedule(requiredLoginNames, skippableLoginNames, daysOfWeek, year, weekOfYear, minHour, maxHour);		
		
		synchronized (unsolvedEventSchedules) {
			unsolvedEventSchedules.put(requestId, unsolvedEventSchedule);
		}
		
		return requestId;
	}

	@Override
	public void startSolving(UUID requestId) {
		if (!isSolving(requestId)) {
			Solver solver = solvers.get(requestId);
			if (solver == null) {
				SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
				solver = solverFactory.buildSolver();
				synchronized (solvers) {
					solvers.put(requestId, solver);
				}
			}
			EventSchedule unsolvedEventSchedule = unsolvedEventSchedules.get(requestId);
			if (unsolvedEventSchedule == null) {
				logger.error("Invalid request id: {}" + requestId);
			}
			solver.solve(unsolvedEventSchedule);
		}
	}

	@Override
	public boolean isSolving(UUID requestId) {
		Solver solver = solvers.get(requestId);
		return solver == null ? false : solver.isSolving();
	}

	@Override
	public void terminateSolvingEearly(UUID requestId) {
		Solver solver = solvers.get(requestId);
		if (solver != null) {
			logger.info("Terminating solving early on request {}", requestId);
			solver.terminateEarly();
		}
	}

	@Override
	public Period getBestPeriod(UUID requestId) {
		Solver solver = solvers.get(requestId);
		if (solver == null) {
			return null;
		}
		EventSchedule solution = (EventSchedule) solver.getBestSolution();
		Optional<Event> resultEvent = solution.getEvents().stream().filter(event -> !event.isLocked()).findFirst();
		return resultEvent.isPresent() ? resultEvent.get().getPeriod() : null;
	}

	@Override
	public User[] getUnavailableUsers(UUID requestId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRequest(UUID requestId) {
		// TODO Auto-generated method stub
		
	}

}
