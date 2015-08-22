<%--
  Created by IntelliJ IDEA.
  User: Adrian
  Date: 25/06/2015
  Time: 10:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="sec"
          uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url value="/logout" var="logoutUrl" />
<html>
<head>
  <title>Main</title>
</head>
<body>
  Main Page.
  <br>
  <form action="${logoutUrl}" method="post">
    <input type="submit" value="Log out" />
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  </form>

  <sec:authorize access="isRememberMe()">
    <h2># This user is login by "Remember Me Cookies".</h2>
  </sec:authorize>

  <sec:authorize access="isFullyAuthenticated()">
    <h2># This user is login by username / password.</h2>
  </sec:authorize>

  <form method="POST" action="/test">

    <label for="testAttr">Test:</label>
    <input type="text" id="testAttr" name="testAttr" value="${testWord}" required>

    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div class="field">
      <button type="submit">Send</button>
    </div>
  </form>

</body>
</html>
