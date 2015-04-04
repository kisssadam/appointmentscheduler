package hu.smartcampus.appointmentscheduler.service;

import hu.smartcampus.appointmentscheduler.domain.EventSchedule;
import hu.smartcampus.appointmentscheduler.domain.EventScheduleFactory;

import java.time.DayOfWeek;
import java.util.UUID;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(endpointInterface = "hu.smartcampus.appointmentscheduler.service.AppointmentSchedulerService")
@SOAPBinding(style = Style.RPC)
public class AppointmentSchedulerServiceImpl implements AppointmentSchedulerService {

	private static final Logger logger = LoggerFactory.getLogger(AppointmentSchedulerServiceImpl.class);
	private static final String SOLVER_CONFIG = "hu/smartcampus/appointmentscheduler/solver/eventScheduleSolverConfig.xml";

	@Override
	public Schedule schedule(String[] requiredLoginNames, String[] skippableLoginNames, DayOfWeek[] daysOfWeek, int year, int weekOfYear, int minHour, int maxHour) {
		UUID requestId = UUID.randomUUID();
		logger.info("Creating new request: {}", requestId);
		SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
		Solver solver = solverFactory.buildSolver();
		
		logger.info("Start solving on request: {}", requestId);
		EventScheduleFactory eventScheduleFactory = new EventScheduleFactory();
		EventSchedule unsolvedEventSchedule = eventScheduleFactory.newEventSchedule(requiredLoginNames, skippableLoginNames, daysOfWeek, year, weekOfYear, minHour, maxHour);
//		EventSchedule unsolvedEventSchedule = EventSchedule.createEventSchedule(requiredLoginNames, skippableLoginNames, daysOfWeek, year, weekOfYear, minHour, maxHour);
		solver.solve(unsolvedEventSchedule);
		
		EventSchedule solvedEventSchedule = (EventSchedule) solver.getBestSolution();
		logger.info("Request {} has been solved: {}", requestId, solvedEventSchedule);
		
		Schedule schedule = new Schedule(solvedEventSchedule);
		return schedule;
	}

}
