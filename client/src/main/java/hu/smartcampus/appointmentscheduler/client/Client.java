package hu.smartcampus.appointmentscheduler.client;

import hu.smartcampus.appointmentscheduler.service.AppointmentScheduler;
import hu.smartcampus.appointmentscheduler.service.Schedule;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
	
	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	
	public static void main(String[] args) throws MalformedURLException {
		logger.info("Starting client.");
//		URL url = new URL("http://localhost:8080/AppointmentSchedulerService/appointmentScheduler?wsdl");
		URL url = new URL("http://localhost:8080/appointmentscheduler?wsdl");
		QName qName = new QName("http://service.appointmentscheduler.smartcampus.hu/", "AppointmentSchedulerImplService");
		
		Service service = Service.create(url, qName);
		AppointmentScheduler appointmentSchedulerService = service.getPort(AppointmentScheduler.class);
		
		String[] requiredLoginNames = new String[] { "KOLLARL", /*"KISSSANDORADAM",*/ "MKOSA", "PANOVICS", /*"BURAIP",*/ "SZAGNES" };
		String[] skippableLoginNames = new String[] { "BURAIP"/*, "VAGNERA"*/ };
		DayOfWeek[] daysOfWeek = { /*DayOfWeek.SUNDAY,*/ DayOfWeek.TUESDAY };
		int year = 2015;
		int weekOfYear = 14;
		int minHour = 8;
		int maxHour = 19;
		
		Schedule schedule = appointmentSchedulerService.schedule(requiredLoginNames, skippableLoginNames, daysOfWeek, year, weekOfYear, minHour, maxHour);
		List<String> availableUserNames = schedule.getAvailableUsers() == null ? new ArrayList<>() : Arrays.stream(schedule.getAvailableUsers()).map(user -> user.getLoginName()).collect(Collectors.toList());
		List<String> unavailableUserNames = schedule.getUnavailableUsers() == null ? new ArrayList<>() : Arrays.stream(schedule.getUnavailableUsers()).map(user -> user.getLoginName()).collect(Collectors.toList());
		System.out.println("Available users: " + availableUserNames);
		System.out.println("Unavailable users: " + unavailableUserNames);
		System.out.println("Year: " + schedule.getYear());
		System.out.println("WeekOfYear: " + schedule.getWeekOfYear());
		System.out.println("DayOfWeek: " + schedule.getDayOfWeek());
		System.out.println("Hour: " + schedule.getHour());
		
		logger.info("Exiting client.");
	}

}
