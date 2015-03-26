package hu.smartcampus.appointmentscheduler.service;

import java.time.DayOfWeek;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import hu.smartcampus.appointmentscheduler.domain.Period;

@WebService
@SOAPBinding(style = Style.RPC)
public interface AppointmentScheduler {

	@WebMethod
	Period getBestPeriod(String[] requiredLoginNames, String[] skippableLoginNames, int year, int weekOfYear, DayOfWeek[] daysOfWeek);
	
}
