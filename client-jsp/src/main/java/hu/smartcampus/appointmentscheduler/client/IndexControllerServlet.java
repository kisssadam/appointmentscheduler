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
@WebServlet("/Controller")
public class IndexControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;
	private final TypedQuery<TUser> userQuery;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IndexControllerServlet() {
		super();
		System.out.println("New controller has been instantiated");
		this.entityManagerFactory = Persistence.createEntityManagerFactory("SMARTCAMPUS");
		this.entityManager = this.entityManagerFactory.createEntityManager();
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
		super.destroy();
		this.entityManagerFactory.close();
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
		List<TUser> queriedUsers = userQuery.getResultList();
		Collections.sort(queriedUsers);
		request.setAttribute("userList", queriedUsers);
		
		request.setAttribute("daysOfWeek", DayOfWeek.values());
		request.setAttribute("now", new Date());
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("WEB-INF/index.jsp");
		requestDispatcher.forward(request, response);
	}

}
