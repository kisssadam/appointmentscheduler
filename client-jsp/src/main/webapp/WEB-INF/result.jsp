<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	request.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<table>
		<tr>
			<td>Available users</td>
			<td>Unavailable users</td>
			<td>Found date</td>
		</tr>
		<tr>
			<td align="center" valign="top">
				<ol>
					<c:forEach items="${schedule.availableUsers}" var="user">
						<li>${user.displayName}</li>
					</c:forEach>
				</ol>
			</td>
			<td align="center" valign="top">
				<ol>
					<c:forEach items="${schedule.unavailableUsers}" var="user">
						<li>${user.displayName}</li>
					</c:forEach>
				</ol>
			</td>
			<td valign="top">
				<table>
					<tr>
						<td>Year:</td>
						<td>${schedule.year}</td>
					</tr>
					<tr>
						<td>Week:</td>
						<td>${schedule.weekOfYear}</td>
					</tr>
					<tr>
						<td>Day:</td>
						<td>${schedule.dayOfWeek}</td>
					</tr>
					<tr>
						<td>Hour:</td>
						<td>${schedule.hour}</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>