<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="login.view.label"/></title>
	</head>
	<body>
		<h3 class="subnav center"><g:message code="login.header.label"/></h3>
		<g:form action="signIn" class="nice-form">
			<input type="hidden" name="targetUri" value="${targetUri}" />
    
			<table class="listing login">
				<tbody>
					<tr><td><label class="login-label"><g:message code="login.username.label"/></label></td></tr>
					<tr><td><input class="login-field" type="text" name="username" value="${username}" /></td></tr>
					<tr><td><label class="login-label"><g:message code="login.password.label"/></label></td></tr>
					<tr><td><input class="login-field" type="password" name="password" value="" /></td></tr>
					<tr><td><g:checkBox name="rememberMe" value="${rememberMe}" /> <label class="login-label"><g:message code="login.rememberme.label"/></label></td></tr>
					<tr><td><ul><li><input type="submit" value="${message(code:'login.signin.label')}" /></li><li><span class="login-label"><a href="${createLink(controller:'auth', action:'forgotPassword')}">Forgot password?</a></span></li></td></tr>
				</tbody>
			</table>
		</g:form>
	</body>
</html>
