package hu.smartcampus.appointmentscheduler.service;

import hu.smartcampus.appointmentscheduler.domain.EventSchedule;
import hu.smartcampus.appointmentscheduler.domain.EventScheduleFactory;

import java.time.DayOfWeek;
import java.util.Arrays;
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
	private static final EventScheduleFactory eventScheduleFactory = new EventScheduleFactory();

	@Override
	public Schedule schedule(String[] requiredLoginNames, String[] skippableLoginNames, DayOfWeek[] daysOfWeek,
			int year, int weekOfYear, int minHour, int maxHour) {
		UUID requestId = UUID.randomUUID();
		logger.info("Creating new request: {}", requestId);
		
		SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
		Solver solver = solverFactory.buildSolver();

		EventSchedule unsolvedEventSchedule;
		try {
			logger.trace(
					"Creating unsolvedEventSchedule on request {} with the following parameters: requiredLoginNames: {}, skippableLoginNames: {}, daysOfWeek: {}, year: {}, weekOfYear: {}, minHour: {}, maxHour: {}.",
					requestId, Arrays.toString(requiredLoginNames), Arrays.toString(skippableLoginNames),
					Arrays.toString(daysOfWeek), year, weekOfYear, minHour, maxHour);
			unsolvedEventSchedule = eventScheduleFactory.newEventSchedule(requiredLoginNames, skippableLoginNames,
					daysOfWeek, year, weekOfYear, minHour, maxHour);
			logger.trace("Finished creating unsolvedEventSchedule on request {}.", requestId);
		} catch (IllegalArgumentException ex) {
			logger.error("Exception on request {}: {}.", requestId, ex.getMessage());
			return null;
		}
		logger.info("Start solving on request: {}", requestId);
		solver.solve(unsolvedEventSchedule);

		EventSchedule solvedEventSchedule = (EventSchedule) solver.getBestSolution();
		logger.info("Request {} has been solved: {}", requestId, solvedEventSchedule);

		return solvedEventSchedule.toSchedule();
	}

}
