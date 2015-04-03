<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
	<div align="center" id="scheduler">
		<form action="index" method="post" accept-charset="UTF-8">
			<input type="hidden" name="action" value="show-result" />
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
						<td><select name="requiredLoginNames" multiple="multiple" size="20" required="required">
								<c:forEach items="${userList}" var="user">
									<option value="${user.loginName}"><c:out value="${user.displayName} - &quot;${user.loginName}&quot;" /></option>
								</c:forEach>
						</select></td>
						<td><select name="skippableLoginNames" multiple="multiple" size="20">
								<c:forEach items="${userList}" var="user">
									<option value="${user.loginName}"><c:out value="${user.displayName} - &quot;${user.loginName}&quot;" /></option>
								</c:forEach>
						</select></td>
						<td>
							<table>
								<tr>
									<td><label>Days:</label></td>
									<td><select name="daysOfWeek" multiple="multiple" size="${fn:length(daysOfWeek)}" required="required">
											<c:forEach items="${daysOfWeek}" var="day">
												<option value="${day}">${day}</option>
											</c:forEach>
									</select></td>
								</tr>
								<tr>
									<td>Year:</td>
									<td align="left"><input type="number" name="year" min="2000" max="9999" value='<fmt:formatDate pattern= "y" value="${now}"/>' required="required" /></td>
								</tr>
								<tr>
									<td>Week:</td>
									<td align="left"><input type="number" name="weekOfYear" min="1" max="52" value='<fmt:formatDate pattern="w" value="${now}"/>' required="required" /></td>
								</tr>
								<tr>
									<td width="20em">Minimum hour:</td>
									<td align="left"><input type="number" name="minHour" min="0" max="23" value="8" required="required" /></td>
								</tr>
								<tr>
									<td>Maximum hour:</td>
									<td align="left"><input type="number" name="maxHour" min="0" max="23" value="19" required="required" /></td>
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