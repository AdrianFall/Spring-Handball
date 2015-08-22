<%--
  Created by IntelliJ IDEA.
  User: Adrian
  Date: 09/05/2015
  Time: 14:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:url value="resources/css/index/index.css" var="indexCss"/>
<!DOCTYPE html>
<head>
  <title>Template</title>
  <link rel="stylesheet" href="${indexCss}"/>
</head>
<body>
  <script>
    window.fbAsyncInit = function() {
      FB.init({
        appId      : '760180054098768',
        xfbml      : true,
        version    : 'v2.3'
      });
    };

    (function(d, s, id){
      var js, fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) {return;}
      js = d.createElement(s); js.id = id;
      js.src = "//connect.facebook.net/en_US/sdk.js";
      fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));
  </script>

  <%-- Include navigation fragment --%>
  <%@include file="fragments/navigation.jspf" %>
  <div class="content-container">
    <div class="content-body">
      <h1 class="template-header">Template</h1>
      <div
              class="fb-like"
              data-share="true"
              data-width="450"
              data-show-faces="true">
      </div>
    </div>
  </div>

  <%-- Include footer --%>
  <%@include file="fragments/footer.jspf" %>
</body>
</html>
