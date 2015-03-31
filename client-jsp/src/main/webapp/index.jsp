<%@page language="java" pageEncoding="utf8" contentType="text/html;charset=UTF-8"%>
<%@page import="java.time.temporal.TemporalField"%>
<%@page import="java.util.Locale"%>
<%@page import="java.time.temporal.WeekFields"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.DayOfWeek"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.List"%>
<%@page import="javax.persistence.EntityManagerFactory"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="javax.persistence.Persistence"%>
<%@page import="javax.persistence.TypedQuery"%>
<%@page import="hu.smartcampus.db.model.TUser"%>
<%@page import="hu.smartcampus.appointmentscheduler.domain.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
input[type="number"] {
	width: 100%;
}

table {
	border: 1px solid black;
	white-space: nowrap;
}
</style>
</head>
<body>
	<h1>Appointment Scheduler</h1>
	<%
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("SMARTCAMPUS");
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		TypedQuery<TUser> query = entityManager.createNamedQuery("TUser.findAll", TUser.class);
		List<TUser> result = query.getResultList();

		List<User> users = new ArrayList<>();
		for (TUser tUser : result) {
			users.add(new User(tUser.getDisplayName(), tUser.getLoginName(), false));
		}
		Collections.sort(users);

		request.setAttribute("userList", users);
	%>
	<div align="center" id="scheduler">
		<form action="result.jsp" method="post" accept-charset="UTF-8">
			<table>
				<thead>
					<tr>
						<td align="center"><label>Required users</label></td>
						<td align="center"><label>Skippable users</label></td>
						<td align="center"><label>Other settings</label></td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td><select name="requiredLoginNames" multiple="multiple" size="20">
								<c:forEach items="${userList}" var="user">
									<option value="${user.loginName}">${user.displayName}-"${user.loginName}"</option>
								</c:forEach>
						</select></td>
						<td><select name="skippableLoginNames" multiple="multiple" size="20">
								<c:forEach items="${userList}" var="user">
									<option value="${user.loginName}">${user.displayName}-"${user.loginName}"</option>
								</c:forEach>
						</select></td>
						<td>
							<table>
								<tr>
									<td><label>Days:</label></td>
									<td><select name="daysOfWeek" multiple="multiple" size="<%=DayOfWeek.values().length%>"
										required="required">
											<%
												for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
											%>
											<option value="<%=dayOfWeek%>">
												<%=dayOfWeek%>
											</option>
											<%
												}
											%>
									</select></td>
								</tr>
								<tr>
									<td>Year:</td>
									<td align="left"><input type="number" name="year" min="2000" max="9999"
										value="<%=LocalDate.now().getYear()%>" /></td>
								</tr>
								<tr>
									<td>Week:</td>
									<%
										TemporalField weekOfYear = WeekFields.of(new Locale("hu_HU")).weekOfYear();
										int weekNumber = LocalDate.now().get(weekOfYear);
									%>
									<td align="left"><input type="number" name="weekOfYear" min="1" max="52" value="<%=weekNumber%>" /></td>
								</tr>
								<tr>
									<td width="20em">Minimum hour:</td>
									<td align="left"><input type="number" name="minHour" min="0" max="23" value="8" /></td>
								</tr>
								<tr>
									<td>Maximum hour:</td>
									<td align="left"><input type="number" name="maxHour" min="0" max="23" value="19" /></td>
								</tr>
							</table>
						</td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="3" align="right"><input type="reset" /> <input type="submit" value="Schedule" /></td>
					</tr>
				</tfoot>
			</table>
		</form>
	</div>
</body>
</html>
