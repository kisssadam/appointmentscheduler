<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.time.DayOfWeek"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
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
		String[] requiredUsers = request.getParameterValues("requiredUsers");
		requiredUsers = requiredUsers == null ? new String[0] : requiredUsers;

		String[] skippableUsers = request.getParameterValues("skippableUsers");
		skippableUsers = skippableUsers == null ? new String[0] : skippableUsers;

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
		
		System.out.println("requiredUsers: " + Arrays.toString(requiredUsers));
		System.out.println("skippableUsers: " + Arrays.toString(skippableUsers));
		System.out.println("daysOfWeek: " + Arrays.toString(daysOfWeek));
		System.out.println("year: " + year);
		System.out.println("weekOfYear: " + weekOfYear);
		System.out.println("minHour: " + minHour);
		System.out.println("maxHour: " + maxHour);
		
		
	%>
</body>
</html>