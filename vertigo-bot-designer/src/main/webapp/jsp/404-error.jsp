<%@ page session="false" import="java.util.*, jakarta.servlet.*" %>
<!-- Icons purchased via Iconfinder under Basic License -->

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="Content-Script-Type" content="text/javascript"/>
	
	<base href="<%= request.getContextPath() + '/' %>"></base>
	<link href="static/css/error.css" type="text/css" rel="stylesheet"/>
	<title>404</title>
</head>

<body class="errorPage">
  <div class="content">
   <h1>404</h1>
   <h2>This page does not exists.</h2>
   <a href="."><button class="denied__link">Go Home</button></a>
  </div>
</body>
</html>