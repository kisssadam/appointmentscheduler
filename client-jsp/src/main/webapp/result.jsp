<%@page import="hu.smartcampus.appointmentscheduler.domain.User"%>
<%@page import="hu.smartcampus.appointmentscheduler.service.Schedule"%>
<%@page
	import="hu.smartcampus.appointmentscheduler.service.AppointmentScheduler"%>
<%@page import="javax.xml.ws.Service"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.time.DayOfWeek"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Enumeration"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		// TODO ha mindket user lista ures, akkor mi van?
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

		System.out.println("requiredUsers: " + Arrays.toString(requiredLoginNames));
		System.out.println("skippableUsers: " + Arrays.toString(skippableLoginNames));
		System.out.println("daysOfWeek: " + Arrays.toString(daysOfWeek));
		System.out.println("year: " + year);
		System.out.println("weekOfYear: " + weekOfYear);
		System.out.println("minHour: " + minHour);
		System.out.println("maxHour: " + maxHour);

		URL url = new URL("http://localhost:8080/AppointmentSchedulerService/appointmentScheduler?wsdl");
		QName qName = new QName("http://service.appointmentscheduler.smartcampus.hu/",
				"AppointmentSchedulerImplService");
		Service service = Service.create(url, qName);
		AppointmentScheduler appointmentSchedulerService = service.getPort(AppointmentScheduler.class);

		Schedule schedule = appointmentSchedulerService.schedule(requiredLoginNames, skippableLoginNames,
				daysOfWeek, year, weekOfYear, minHour, maxHour);
		/* List<String> availableUserNames = schedule.getAvailableUsers() == null ? new ArrayList<>() : Arrays.stream(schedule.getAvailableUsers()).map(user -> user.getLoginName()).collect(Collectors.toList());
		List<String> unavailableUserNames = schedule.getUnavailableUsers() == null ? new ArrayList<>() : Arrays.stream(schedule.getUnavailableUsers()).map(user -> user.getLoginName()).collect(Collectors.toList()); */
		/* System.out.println("Available users: " + availableUserNames);
		System.out.println("Unavailable users: " + unavailableUserNames); */
		System.out.println("Year: " + schedule.getYear());
		System.out.println("WeekOfYear: " + schedule.getWeekOfYear());
		System.out.println("DayOfWeek: " + schedule.getDayOfWeek());
		System.out.println("Hour: " + schedule.getHour());
	%>
	<table>
		<tr>
			<td>Available users</td>
			<td>Unavailable users</td>
			<td>Found date</td>
		</tr>
		<tr>
			<td align="left">
				<ol>
					<%
						User[] availableUsers = schedule.getAvailableUsers();
						availableUsers = availableUsers == null ? new User[0] : availableUsers;

						for (User availableUser : availableUsers) {
					%>
					<li><%=availableUser.getDisplayName()%></li>
					<%
						}
					%>
				</ol>
			</td>
			<td align="left">
				<ol>
					<%
						User[] unavailableUsers = schedule.getUnavailableUsers();
						unavailableUsers = unavailableUsers == null ? new User[0] : unavailableUsers;

						for (User unavailableUser : unavailableUsers) {
					%>
					<li><%=unavailableUser.getDisplayName()%></li>
					<%
						}
					%>
				</ol>
			</td>
			<td>
				<table>
					<tr>
						<td>Year:</td>
						<td><%=schedule.getYear()%></td>
					</tr>
					<tr>
						<td>Week:</td>
						<td><%=schedule.getWeekOfYear()%></td>
					</tr>
					<tr>
						<td>Day:</td>
						<td><%=schedule.getDayOfWeek()%></td>
					</tr>
					<tr>
						<td>Hour:</td>
						<td><%=schedule.getHour()%></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>