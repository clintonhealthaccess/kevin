<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="new.password.view.label"/></title>
	</head>
	<body>
		<h3 class="subnav center"><g:message code="new.password.header.label" default="New password."/></h3>
		<g:form action="setPassword" class="nice-form">
			<input type="hidden" name="targetUri" value="${targetUri}" />
    		<input type="hidden" name="token" value="${token}" />
    
			<table class="listing login">
				<tbody>
					<tr><td><label class="login-label"><g:message code="new.password.password.label" default="New password"/></label></td></tr>
					<tr>
						<td>
							<input class="login-field" type="password" name="password" value="" />
						</td>
					</tr>
					<tr><td><label class="login-label"><g:message code="new.password.repeat.label" default="Repeat password"/></label></td></tr>
					<tr><td><input class="login-field" type="password" name="repeat" value="" /></td></tr>
					
					<tr><td><input type="submit" value="${message(code:'new.password.set.label', default:'Set password')}" /></td></tr>
				</tbody>
			</table>
		</g:form>
	</body>
</html>
