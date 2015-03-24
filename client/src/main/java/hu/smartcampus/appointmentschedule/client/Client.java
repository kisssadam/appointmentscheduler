package hu.smartcampus.appointmentschedule.client;

import hu.smartcampus.appointmentschedule.domain.Period;
import hu.smartcampus.appointmentschedule.service.AppointmentSchedule;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Client {
	
	public static void main(String[] args) throws MalformedURLException {
//		URL url = new URL("http://localhost:8080/AppointmentScheduleService/appointmentSchedule?wsdl");
		URL url = new URL("http://localhost:8080/appointmentschedule?wsdl");
		QName qName = new QName("http://service.appointmentschedule.smartcampus.hu/", "AppointmentScheduleImplService");
		
		Service service = Service.create(url, qName);
		AppointmentSchedule appointmentScheduleService = service.getPort(AppointmentSchedule.class);
		
		int year = 2015;
		int weekOfYear = 12;
		String[] requiredLoginNames = new String[] { "KOLLARL", "KISSSANDORADAM", "MKOSA", "PANOVICS" };
		String[] skippableLoginNames = new String[] { "BURAIP", "VAGNERA" };
		DayOfWeek[] daysOfWeek = { DayOfWeek.TUESDAY };
		
		Period period = appointmentScheduleService.getBestPeriod(requiredLoginNames, skippableLoginNames, year, weekOfYear, daysOfWeek);
		
		System.out.println(period);
	}

}
