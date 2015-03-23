package hu.smartcampus.appointmentschedule.service;

import javax.xml.ws.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Publish {

	private static final Logger logger = LoggerFactory.getLogger(Publish.class);

	public static void main(String[] args) throws InterruptedException {
		Endpoint endpoint = Endpoint.publish("http://localhost:8080/appointmentschedule", new AppointmentScheduleImpl());
		logger.info("Endpoint has" + (endpoint.isPublished() ? " been published." : "n't been published"));
	}

}
