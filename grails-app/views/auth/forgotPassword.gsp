<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="forgot.password.view.label"/></title>
	</head>
	<body>
		<h3 class="subnav center"><g:message code="forgot.password.header.label"/></h3>
		<g:form action="retrievePassword" class="nice-form">
    
			<table class="listing login">
				<tbody>
					<tr><td><label class="login-label"><g:message code="forgot.password.email.label"/></label></td></tr>
					<tr>
						<td>
							<input class="login-field" type="text" name="email" value="${retrievePassword?.email}" />
							<div class="error-list"><g:renderErrors bean="${retrievePassword}" field="email" /></div>
						</td>
					</tr>
					<tr><td><ul><li><input type="submit" value="${message(code:'forgot.password.retrieve.label')}" /></li><li><span class="login-label"><g:message code="forgot.password.info.text"/></span></li></td></tr>
				</tbody>
			</table>
		</g:form>
	</body>
</html>
