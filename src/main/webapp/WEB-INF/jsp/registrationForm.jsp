
<%--
  Created by IntelliJ IDEA.
  User: Adrian
  Date: 10/05/2015
  Time: 16:16
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<spring:url value="resources/css/registration/registrationForm.css" var="registrationFormCss"/>
<html>
<head>
  <title>Sample Form</title>

  <link rel="stylesheet" href="${registrationFormCss}"/>

</head>
<body>

  <%-- Include navigation fragment --%>
  <%@include file="fragments/navigation.jspf" %>

  <form:form class="regForm" action="/register" method="POST" modelAttribute="registrationForm">
    <label for="emailInput"><spring:message code="registration.email"/>:</label>
    <form:input type="email" path="email" id="emailInput" />
    <form:errors path="email" cssClass="error" />
    <br/>

    <%--<c:if test="${registrationForm.signInProvider == null}">--%>
    <label for="passwordInput"><spring:message code="registration.password"/>:</label>
    <form:input type="password" path="password" id="passwordInput" />
    <form:errors path="password" cssClass="error" />
    <br/>

    <label for="confirmPasswordInput"><spring:message code="registration.confirmPassword"/>:</label>
    <form:input type="password" path="confirmPassword" id="confirmPasswordInput" />
    <form:errors path="confirmPassword" cssClass="error" />
    <br/>

    <input class="submit" type="submit" value="<spring:message code="registration.submit"/>" />
  </form:form>
</body>
</html>
