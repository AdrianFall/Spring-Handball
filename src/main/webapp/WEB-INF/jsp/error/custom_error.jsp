<%--
  Created by IntelliJ IDEA.
  User: Adrian
  Date: 27/06/2015
  Time: 14:15
  To change this template use File | Settings | File Templates.
--%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>

  <title>Error</title>
</head>
<body>
  <h1> Error Page </h1>
  <p>The application has encountered an error:</p>
  <p>Failed URL : ${url}</p>
  <p>Exception : ${exception.message}</p>

  <c:forEach items="${exception.stackTrace}" var="ste">
    ${ste}
  </c:forEach>
</body>
</html>
