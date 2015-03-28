package hu.smartcampus.appointmentscheduler.client;

import hu.smartcampus.appointmentscheduler.domain.Period;
import hu.smartcampus.appointmentscheduler.service.AppointmentScheduler;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.util.UUID;

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
		
		String[] requiredLoginNames = new String[] { "KOLLARL", /*"KISSSANDORADAM",*/ /*"MKOSA", "PANOVICS",*/ "BURAIP", "SZAGNES" };
		String[] skippableLoginNames = new String[] { /*"BURAIP", "VAGNERA"*/ };
		DayOfWeek[] daysOfWeek = { /*DayOfWeek.SUNDAY,*/ DayOfWeek.TUESDAY };
		int year = 2015;
		int weekOfYear = 14;
		int minHour = 8;
		int maxHour = 16;
		
		logger.info("Calling createAppointmentScheduler().");
		UUID requestId = appointmentSchedulerService.createAppointmentScheduler(requiredLoginNames, skippableLoginNames, daysOfWeek, year, weekOfYear, minHour, maxHour);
		logger.info("Request id: {}", requestId);
		
		appointmentSchedulerService.startSolving(requestId);
		Period period = appointmentSchedulerService.getBestPeriod(requestId);
		
		System.out.println(period);
		
		logger.info("Exiting client.");
	}

}
