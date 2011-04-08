<%@ page language="java" contentType="text/html; utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<body>
<h2>up+running - hi to <sec:authentication property="principal.username" />!</h2>

<form action="logout" method="post">
  <label for="submit">Logout</label>
  <input name="submit" type="submit" value="Logout"/>
</form>

<sec:authorize access="hasRole('ROLE_GUEST')">
	As a guest you have only restricted access - clicking on the link below is forbidden!
	<c:url value="/forbiddenforguests.do" var="forbiddenUrl"/>
	<li><a href="${forbiddenUrl}">Trigger Forbidden Method Call</a></li>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_USER')">
	You have full access to all functions
	<c:url value="/forbiddenforguests.do" var="forbiddenUrl"/>
	<li><a href="${forbiddenUrl}">Trigger Forbidden Method Call</a></li>
</sec:authorize>

</body>
</html>
