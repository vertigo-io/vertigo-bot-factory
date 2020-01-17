<%@ page session="false" import="java.util.*, javax.servlet.*" %>
<!-- Icons purchased via Iconfinder under Basic License -->
<%
     String baseUrl = request.getRequestURL().substring(0, request.getRequestURL().indexOf(request.getServletPath()))+"/";
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="Content-Script-Type" content="text/javascript"/>
	
	<base href="<%=baseUrl%>"/>	
	<link href="static/css/error.css" type="text/css" rel="stylesheet"/>
	<title>404</title>
</head>

<body class="permission_denied">
  <div class="denied__wrapper">
    <h1>404</h1>
    <a href="."><button class="denied__link">Go Home</button></a>
  </div>
      
</body>
</html>