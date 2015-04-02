<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">
thead {
	text-align: center;
}

table.center {
	margin-left: auto;
	margin-right: auto;
}

td {
	vertical-align: top;
}
</style>
</head>
<fmt:setLocale value="hu_HU" />
<body>
	<table class="center">
		<thead>
			<tr>
				<td colspan="2"><fmt:formatDate value="${schedule.date}" pattern="yyyy. MMMM dd. (EEEE) H:mm" />
					<hr /></td>
			</tr>
			<tr>
				<td>Available users</td>
				<td>Unavailable users</td>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>
					<ol>
						<c:forEach items="${schedule.availableUsers}" var="user">
							<li>${user.displayName}</li>
						</c:forEach>
					</ol>
				</td>
				<td>
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