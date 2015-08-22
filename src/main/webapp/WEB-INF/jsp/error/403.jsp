<%--
  Created by IntelliJ IDEA.
  User: Adrian
  Date: 27/06/2015
  Time: 14:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:url value="resources/css/error/404/404.css" var="css" />
<html>
<head>
  <title>403#</title>
  <link rel="stylesheet" href="${css}"/>
</head>
<body>
<%@include file="../fragments/navigation.jspf"%>
<p class="para">Forbidden</p>
</body>
</html>
