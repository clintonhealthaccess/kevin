<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="register.view.label"/></title>
	</head>
	<body>
		<h3 class="subnav center"><g:message code="register.header.label"/></h3>
		<g:form action="sendRegistration" class="nice-form">
    
			<table class="listing login">
				<tbody>
					<tr><td><label class="login-label"><g:message code="register.firstname.label" default="First Name"/></label></td></tr>
					<tr>
						<td>
							<input class="login-field" type="text" name="firstname" value="${register?.firstname}" />
							<div class="error-list"><g:renderErrors bean="${register}" field="firstname" /></div>
						</td>
					</tr>
					
					<tr><td><label class="login-label"><g:message code="register.lastname.label" default="Last Name"/></label></td></tr>
					<tr>
						<td>
							<input class="login-field" type="text" name="lastname" value="${register?.lastname}" />
							<div class="error-list"><g:renderErrors bean="${register}" field="lastname" /></div>
						</td>
					</tr>
					
					<tr><td><label class="login-label"><g:message code="register.organisation.label" default="Organisation"/></label></td></tr>
					<tr>
						<td>
							<input class="login-field" type="text" name="organisation" value="${register?.organisation}" />
							<div class="error-list"><g:renderErrors bean="${register}" field="organisation" /></div>
						</td>
					</tr>
					
					<tr><td><label class="login-label"><g:message code="register.email.label"/></label></td></tr>
					<tr>
						<td>
							<input class="login-field" type="text" name="email" value="${register?.email}" />
							<div class="error-list"><g:renderErrors bean="${register}" field="email" /></div>
						</td>
					</tr>
					
					<tr><td><label class="login-label"><g:message code="register.password.label" default="Password"/></label></td></tr>
					<tr>
						<td>
							<input class="login-field" type="password" name="password" value="" />
							<div class="error-list"><g:renderErrors bean="${register}" field="password" /></div>
						</td>
					</tr>
					<tr><td><label class="login-label"><g:message code="register.repeat.label" default="Repeat password"/></label></td></tr>
					<tr>
						<td>
							<input class="login-field" type="password" name="repeat" value="" />
							<div class="error-list"><g:renderErrors bean="${register}" field="repeat" /></div>
						</td>
					</tr>
					
					<tr><td><ul><li><input type="submit" value="${message(code:'register.register.label')}" /></li><li><span class="login-label"><g:message code="register.info.text"/></span></li></td></tr>
				</tbody>
			</table>
		</g:form>
	</body>
</html>
