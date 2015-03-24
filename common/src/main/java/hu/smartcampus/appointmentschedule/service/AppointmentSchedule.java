package hu.smartcampus.appointmentschedule.service;

import java.time.DayOfWeek;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import hu.smartcampus.appointmentschedule.domain.Period;

@WebService
@SOAPBinding(style = Style.RPC)
public interface AppointmentSchedule {

	@WebMethod
	Period getBestPeriod(String[] requiredLoginNames, String[] skippableLoginNames, int year, int weekOfYear, DayOfWeek[] daysOfWeek);
	
}
