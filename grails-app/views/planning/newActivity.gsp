<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.newActivity.label" default="District Health System Portal" /></title>
		
		<r:require module="survey"/>
	</head>
	<body>
		<div id="survey">
			<div class="main">  
				<p class="help">
			  		<g:message code="survey.welcomemessage.label" default="Welcome to the survey, please fill in all the objectives above."/>
				</p>
			</div>
			
			<g:each in="${activityType.sections}" var="section">
				<g:i18n field="${activityType.headers[section]}"/>

							
			</g:each>
		</div>
	</body>
</html>