package client;

import hu.smartcampus.appointmentschedule.domain.Day;
import hu.smartcampus.appointmentschedule.domain.Period;
import hu.smartcampus.appointmentschedule.service.AppointmentSchedule;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Client {
	
	public static void main(String[] args) throws MalformedURLException {
		URL url = new URL("http://localhost:8080/appointmentschedule?wsdl");
		QName qName = new QName("http://service.appointmentschedule.smartcampus.hu/", "AppointmentScheduleImplService");
		
		Service service = Service.create(url, qName);
		AppointmentSchedule appointmentScheduleService = service.getPort(AppointmentSchedule.class);
		
//		DayOfWeek
		
		int year = 2015;
		int weekOfYear = 12;
		String[] requiredLoginNames = new String[] { "KOLLARL", "KISSSANDORADAM", "MKOSA", "PANOVICS" };
		String[] skippableLoginNames = new String[] { "BURAIP", "VAGNERA" };
//		Day[] days = { Day.MONDAY, Day.TUESDAY, Day.FRIDAY };
		Day[] days = { Day.TUESDAY };
		
		Period period = appointmentScheduleService.getBestPeriod(requiredLoginNames, skippableLoginNames, year, weekOfYear, days);
		
		System.out.println(period);
	}

}
