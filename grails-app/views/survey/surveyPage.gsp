<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.surveyPage.label" /></title>
		
		<r:require module="survey"/>
	</head>
	<body>
		<div>
			<g:render template="/survey/header" model="[period: surveyPage.period, location: surveyPage.location]"/>
			<div class="main">  
				<p class="nav-help">
			  		<g:message code="survey.welcomemessage.label"/>
				</p>
			</div>
		</div>
	</body>
</html>