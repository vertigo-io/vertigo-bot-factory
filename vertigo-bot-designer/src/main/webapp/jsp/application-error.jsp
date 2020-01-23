<%@ page session="false" import="java.util.*, javax.servlet.*" %>
<%!
	
private String printException(Throwable t) throws Exception {
        int i;
        StringBuffer sw = new StringBuffer();
        StackTraceElement[] stack = t.getStackTrace();
        sw.append(t.getClass().getName());
        sw.append(": <h4>").append(t.getMessage()).append("</h4>");
        for (i = 0; i < stack.length; i++) {
            if (stack[i].getClassName().startsWith("javax.servlet.")) {
                sw.append("...");
                break;
            }
            sw.append("\n\t").append(stack[i]);
        }
        String res = sw.toString();
		for (i = 0; (i = res.indexOf('&', i)) >= 0; i++) res = res.substring(0, i) + "&amp;" + res.substring(i + 1);
	   	for (i = 0; (i = res.indexOf('\"', i)) >= 0;) res = res.substring(0, i) + "&quot;" + res.substring(i + 1);
    	for (i = 0; (i = res.indexOf('<', i)) >= 0;) res = res.substring(0, i) + "&lt;" + res.substring(i + 1);
    	for (i = 0; (i = res.indexOf('>', i)) >= 0;) res = res.substring(0, i) + "&gt;" + res.substring(i + 1);
    	for (i = 0; (i = res.indexOf('\t', i)) >= 0;) res = res.substring(0, i) + "&nbsp;&nbsp;" + res.substring(i + 1);
    	for (i = 0; (i = res.indexOf('\n', i)) >= 0; i++) res = res.substring(0, i + 1) + "<br/>" + res.substring(i + 1);
		for (i = 0; (i = res.indexOf("&lt;h4&gt;", i)) >= 0;) res = res.substring(0, i) + "<h4>" + res.substring(i + "&lt;h4&gt;".length());
    	for (i = 0; (i = res.indexOf("&lt;/h4&gt;", i)) >= 0; i++) res = res.substring(0, i) + "</h4>" + res.substring(i + "&lt;/h4&gt;".length());
		return res;
    }
%>

<%
		final String baseUrl = request.getContextPath()+"/";

		Throwable e = (Throwable) request.getAttribute("javax.servlet.error.exception"), t;
		String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
		Integer errorCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		//Throwable e = (Throwable)pageContext.getAttribute("ex"), t;
        List list = new ArrayList();
        String message = null;
        boolean sessionException = false;
        boolean contextException = false;
        boolean securityException = false;
        boolean noObjectFoundException = false;
       	
        if (errorCode!=null && errorCode.equals(401)) {
        	response.sendRedirect(baseUrl+"login/?code=401");
        }
        
        for (; e != null;) {
            list.add(e);
            if (e instanceof io.vertigo.vega.webservice.exception.SessionException) {
            	sessionException = true;
            	response.sendRedirect(baseUrl+"login/?code=400");
            	%>
            	<%-- jsp:forward page="noSession-error.jsp" / --%>
            <%}
            if (e instanceof io.vertigo.ui.exception.ExpiredViewContextException) contextException = true;
            if (e instanceof io.vertigo.account.authorization.VSecurityException || errorCode == 403) securityException = true;
            t = e.getCause();
            if (t == null && (e instanceof ServletException)) t = ((ServletException) e).getRootCause();
            if (t == null && (e instanceof java.sql.SQLException)) t = ((java.sql.SQLException) e).getNextException();
            if (t == null && (e instanceof io.vertigo.lang.WrappedException)) t = ((io.vertigo.lang.WrappedException) e).getCause();
            e = t;
        }
        Collections.reverse(list);
       
       StringBuilder sbHome = new StringBuilder("");
 	   sbHome.append("<a class=\"lien\" href=\"home/\">");
 	   sbHome.append("<button class=\"fix_link\">l'&eacute;cran d'accueil</button>");
 	   sbHome.append("</a>");
 	   
 	   StringBuilder sbPrevious = new StringBuilder("");
 	   sbPrevious.append("<a class=\"lien\" href=\"javascript:history.back();\">");
 	   sbPrevious.append("<button class=\"fix_link\">l'&eacute;cran pr&eacute;c&eacute;dent</button>");
 	   sbPrevious.append("</a>");
 	   
 	   StringBuilder sbReconnect = new StringBuilder("");
 	   sbReconnect.append("<a class=\"lien\" href=\"login/\">");
 	   sbReconnect.append("<button class=\"fix_link\">reconnecter</button>");
 	   sbReconnect.append("</a>");
 	   
 	   StringBuilder sbReconnectAccent = new StringBuilder("");
 	   sbReconnectAccent.append("<a class=\"lien\" href=\"login/\">");
 	   sbReconnectAccent.append("<button class=\"fix_link\">reconnect&eacute;</button>");
 	   sbReconnectAccent.append("</a>");
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="Content-Script-Type" content="text/javascript"/>
	
	<base href="<%=baseUrl%>"/>	
	<link href="static/css/error.css" type="text/css" rel="stylesheet"/>
	<title><%=errorCode%></title>
</head>

<body class="errorPage">
	<script>
		var isVisible = false;
		handleClick = function () {
			if (document.getElementById) {
				if(!isVisible)
					document.getElementById("errordetail").style.display = "block";
				else
			    	document.getElementById("errordetail").style.display = "none";
			    isVisible = !isVisible;
			  }
			  else {
			    document.write("Unrecognized Browser Detected");
			  }
			}
	</script>
  <div>
  	<div class="content">
    <h1><%=errorCode%></h1>
    <% if (sessionException) { %>
		<h2>Votre session a expir&eacute;</h2>
		<p>
		Vous n'avez pas utilis&eacute; l'application pendant un certain temps, pour des raisons de s&eacute;curit&eacute; l'application vous a donc d&eacute;connect&eacute;.<br/>
		Vous pourrez reprendre votre travail apr&eacute;s vous &eacute;tre <%=sbReconnectAccent.toString()%>.
		</p>
	<% } else if (contextException) { %>
		<h2>Le contexte de cet &eacute;cran n'est plus disponible</h2>
		<p>
		L'action que vous avez demand&eacute;e ne peut se poursuivre car le contexte de cet &eacute;cran n'est plus disponible.<br/>
		Vous pouvez continuer votre travail &eacute; partir de <%=sbPrevious.toString()%> ou <%=sbHome.toString()%>.<br/>
		Ce cas de figure peut se produire si vous avez cliqu&eacute; un trop grand nombre de fois de suite sur la fl&eacute;che "retour arri&eacute;re" de votre navigateur, ou si vous avez attendu trop longtemps avant de cliquer sur une action.
		</p>
	<% } else if (securityException) { %>
		<h2>Vous n'&eacute;tes pas habilit&eacute; &eacute; effectuer cette action</h2>
		<p>
		L'action que vous avez demand&eacute;e ne peut se poursuivre car vous n'avez pas les habilitations suffisantes.<br/>
		Vous pouvez continuer votre travail &eacute; partir de <%=sbPrevious.toString()%> ou <%=sbHome.toString()%>.<br/>
		Vous pouvez &eacute;galement vous <%=sbReconnect.toString()%>.
		</p>
	<% } else if (noObjectFoundException) { %>
		<h2>L'&eacute;l&eacute;ment demand&eacute; n'existe pas </h2>
		<p>
		L'&eacute;l&eacute;ment demand&eacute; n'existe pas, il vient probablement d'&eacute;tre supprim&eacute; par un autre utilisateur.<br/>
		Vous pouvez continuer votre travail &agrave; partir de <%=sbPrevious.toString()%> ou <%=sbHome.toString()%>.<br/>
		</p>
	<% } else { %>
		<h2>Erreur de l'application</h2>
<p>Cet &eacute;cran signifie qu'une erreur syst&egrave;me inattendue s'est produite pendant le traitement de l'action demand&eacute;e.<br/>
Un enregistrement a &eacute;t&eacute; cr&eacute;&eacute; dans le fichier de log de l'application.</p>
<p>De telles erreurs peuvent se produire en cas d'indisponibilit&eacute; temporaire d'un composant syst&egrave;me, dans ce cas,<br/>
vous pouvez revenir &agrave; <%=sbPrevious.toString()%> puis essayer &agrave; nouveau.</p>
<p>Il peut aussi s'agir d'une anomalie de l'application, dans ce cas, veuillez contacter le support technique et leur<br/>
communiquer l'heure &agrave; laquelle s'est produite l'erreur ainsi que les informations ci dessous.</p>
		
		<% 
			final io.vertigo.core.param.ParamManager paramManager = io.vertigo.app.Home.getApp().getComponentSpace().resolve(io.vertigo.core.param.ParamManager.class);
			final Optional<io.vertigo.core.param.Param> devModeOpt = paramManager.getOptionalParam("devMode");
			if (devModeOpt.isPresent() && devModeOpt.get().getValueAsBoolean()) { %>
			<a href="#" onclick="handleClick();return false;" id="showerrorlink"><button class="denied__link">Voir le message d'erreur</button></a>	
			</div>
			<div id="errordetail" style="display:none;">
			<h2><%="HTTP (" + errorCode + ") : " + errorMessage %></h2>
			<% for (int i = 0; i < list.size(); i++) { %>
				<% t = (Throwable)list.get(i); %>
				<h4><%= i > 0 ? "Cons&eacute;quence (" + i + ")" : "Cause racine" %></h4>
				<div class="code">
					<%= printException(t)%>
				</div>
			<% } %>
			</div>
		<% } else {%>
			</div>
		<% } %>
	<% } %>
       
  </div>
      
  </body>
  </html>