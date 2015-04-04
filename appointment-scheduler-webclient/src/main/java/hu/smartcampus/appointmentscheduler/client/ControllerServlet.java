package hu.smartcampus.appointmentscheduler.client;

import hu.smartcampus.appointmentscheduler.entity.TUser;
import hu.smartcampus.appointmentscheduler.service.AppointmentSchedulerService;
import hu.smartcampus.appointmentscheduler.service.Schedule;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet
public class ControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final EntityManagerFactory entityManagerFactory;
	private static final Logger logger = LoggerFactory.getLogger(ControllerServlet.class);
	private final EntityManager entityManager;
	private final TypedQuery<TUser> userQuery;

	static {
		entityManagerFactory = Persistence.createEntityManagerFactory("SMARTCAMPUS");
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ControllerServlet() {
		super();
		logger.trace("Instantiating servlet. Creating entity manager.");
		this.entityManager = entityManagerFactory.createEntityManager();
		this.userQuery = this.entityManager.createNamedQuery("TUser.findAll", TUser.class);
	}

	@Override
	public void init() throws ServletException {
		prefetchUsers();
	}

	private void prefetchUsers() {
		logger.trace("Prefetching users.");
		userQuery.getResultList();
	}

	@Override
	public void destroy() {
		logger.trace("Destroying servlet. Closing entity manager.");
		if (this.entityManager.isOpen()) {
			this.entityManager.close();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");
		action = action == null ? "show-input-form" : action;

		switch (action) {
		case "show-result":
			showResult(request, response);
			break;

		case "show-input-form":
			showInputForm(request, response);
			break;

		default:
			showInputForm(request, response);
			break;
		}
	}

	private void showInputForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<TUser> queriedUsers = userQuery.getResultList();
		Collections.sort(queriedUsers);
		request.setAttribute("userList", queriedUsers);

		request.setAttribute("daysOfWeek", DayOfWeek.values());
		request.setAttribute("now", new Date());

		dispatch(request, response, "WEB-INF/index.jsp");
	}

	private void showResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] requiredLoginNames = request.getParameterValues("requiredLoginNames");
		requiredLoginNames = requiredLoginNames == null ? new String[0] : requiredLoginNames;

		String[] skippableLoginNames = request.getParameterValues("skippableLoginNames");
		skippableLoginNames = skippableLoginNames == null ? new String[0] : skippableLoginNames;

		String[] dayOfWeekStrings = request.getParameterValues("daysOfWeek");
		List<DayOfWeek> dayOfWeekList = new ArrayList<>();
		for (String dayOfWeekString : dayOfWeekStrings) {
			dayOfWeekList.add(DayOfWeek.valueOf(dayOfWeekString));
		}
		DayOfWeek[] daysOfWeek = dayOfWeekList.toArray(new DayOfWeek[dayOfWeekList.size()]);

		int year = Integer.parseInt(request.getParameter("year"));
		int weekOfYear = Integer.parseInt(request.getParameter("weekOfYear"));
		int minHour = Integer.parseInt(request.getParameter("minHour"));
		int maxHour = Integer.parseInt(request.getParameter("maxHour"));

		logger.trace("Trying to access the appointment scheduler service.");
		URL url = new URL("http://localhost:8080/AppointmentSchedulerService/appointmentscheduler?wsdl");
		QName qName = new QName("http://service.appointmentscheduler.smartcampus.hu/", "AppointmentSchedulerServiceImplService");
		Service service = Service.create(url, qName);
		AppointmentSchedulerService appointmentSchedulerService = service.getPort(AppointmentSchedulerService.class);

		Schedule schedule = appointmentSchedulerService.schedule(requiredLoginNames, skippableLoginNames, daysOfWeek, year,
				weekOfYear, minHour, maxHour);
		request.setAttribute("schedule", schedule);

		dispatch(request, response, "WEB-INF/result.jsp");
	}

	private void dispatch(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException,
			IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}

}
