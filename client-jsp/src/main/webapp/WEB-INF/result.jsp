<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<p>
		<fmt:setLocale value="hu_HU" />
		<fmt:formatDate value="${schedule.date}" pattern="yyyy. MMMM dd. (EEEE) H:mm" />
	</p>
	<table>
		<thead>
			<tr>
				<td>Available users</td>
				<td>Unavailable users</td>
			</tr>
		</thead>
		<tbody>
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
			</tr>
		</tbody>
	</table>
</body>
</html>