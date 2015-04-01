package hu.smartcampus.appointmentscheduler.client;

import hu.smartcampus.db.model.TUser;

import java.io.IOException;
import java.time.DayOfWeek;
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

/**
 * Servlet implementation class Controller
 */
@WebServlet
public class ControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final EntityManagerFactory ENTITY_MANAGER_FACTORY;
	private final EntityManager entityManager;
	private final TypedQuery<TUser> userQuery;

	static {
		ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("SMARTCAMPUS");
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ControllerServlet() {
		super();
		System.out.println("New controller has been instantiated");
		this.entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
		this.userQuery = this.entityManager.createNamedQuery("TUser.findAll", TUser.class);
	}

	@Override
	public void init() throws ServletException {
		prefetchUsers();
	}

	private void prefetchUsers() {
		userQuery.getResultList();
	}

	@Override
	public void destroy() {
		this.entityManager.close();
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
		String servletPath = request.getServletPath();
		System.out.println(servletPath);
		switch (servletPath) {
		case "/index":
			List<TUser> queriedUsers = userQuery.getResultList();
			Collections.sort(queriedUsers);
			request.setAttribute("userList", queriedUsers);

			request.setAttribute("daysOfWeek", DayOfWeek.values());
			request.setAttribute("now", new Date());

			dispatch(request, response, "WEB-INF/index.jsp");
			// RequestDispatcher requestDispatcher = request.getRequestDispatcher("WEB-INF/index.jsp");
			// requestDispatcher.forward(request, response);
			break;

		case "/show-result":
			dispatch(request, response, "WEB-INF/result.jsp");
			break;

		default:
			break;
		}

	}

	private void dispatch(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}

}
