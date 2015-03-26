package hu.smartcampus.appointmentscheduler.client;

import hu.smartcampus.appointmentscheduler.domain.Period;
import hu.smartcampus.appointmentscheduler.service.AppointmentScheduler;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;

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
		
		int year = 2015;
		int weekOfYear = 12;
		String[] requiredLoginNames = new String[] { "KOLLARL", "KISSSANDORADAM", "MKOSA", "PANOVICS" };
		String[] skippableLoginNames = new String[] { "BURAIP", "VAGNERA" };
		DayOfWeek[] daysOfWeek = { DayOfWeek.TUESDAY };
		
		Period period = appointmentSchedulerService.getBestPeriod(requiredLoginNames, skippableLoginNames, year, weekOfYear, daysOfWeek);
		
		System.out.println(period);
		logger.info("Exiting client.");
	}

}
