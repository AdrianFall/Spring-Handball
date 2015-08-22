<%--
  Created by IntelliJ IDEA.
  User: Adrian
  Date: 29/06/2015
  Time: 14:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ page session="false"%>
<html>
<head>
  <title><spring:message code="reset.password.title"/></title>
</head>
<body>
<%-- Include navigation fragment --%>
<%@include file="fragments/navigation.jspf" %>
<h1><spring:message code="reset.password.title"/></h1>
<sec:authorize access="hasRole('ROLE_USER')">
  <form:form class="regForm" action="/resetPassword" method="POST" modelAttribute="resetPasswordForm">

    <label for="passwordInput"><spring:message code="reset.password.newpassword"/>:</label>
    <form:input type="password" path="password" id="passwordInput" />
    <form:errors path="password" cssClass="error" />
    <br/>

    <label for="confirmPasswordInput"><spring:message code="reset.password.newpasswordconfirm"/>:</label>
    <form:input type="password" path="confirmPassword" id="confirmPasswordInput" />
    <form:errors path="confirmPassword" cssClass="error" />
    <br/>
    <input class="submit" type="submit" value="<spring:message code="reset.password.submit"/>" />
  </form:form>
</sec:authorize>
</body>
</html>
