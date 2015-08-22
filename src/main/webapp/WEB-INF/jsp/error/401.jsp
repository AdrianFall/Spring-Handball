
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:url value="resources/css/error/404/404.css" var="css" />
<html>
<head>
  <title>401</title>
  <link rel="stylesheet" href="${css}"/>
</head>
<body>
<%@include file="../fragments/navigation.jspf"%>
<p class="para">Unauthorized</p>
</body>
</html>
