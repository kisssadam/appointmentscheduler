package hu.smartcampus.appointmentscheduler.service;

import java.time.DayOfWeek;
import java.util.UUID;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import hu.smartcampus.appointmentscheduler.domain.Period;
import hu.smartcampus.appointmentscheduler.domain.User;

@WebService
@SOAPBinding(style = Style.RPC)
public interface AppointmentScheduler {

	@WebMethod
	UUID createAppointmentScheduler(String[] requiredLoginNames, String[] skippableLoginNames, DayOfWeek[] daysOfWeek, int year, int weekOfYear, int minHour, int maxHour);

	@WebMethod
	void startSolving(UUID requestId);
	
	@WebMethod
	boolean isSolving(UUID requestId);
	
	@WebMethod
	void terminateSolvingEearly(UUID requestId);

	@WebMethod
	Period getBestPeriod(UUID requestId);

	@WebMethod
	User[] getUnavailableUsers(UUID requestId);
	
	@WebMethod
	void deleteRequest(UUID requestId);
	
}
