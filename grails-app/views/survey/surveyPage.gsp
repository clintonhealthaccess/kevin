<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.surveyPage.label" default="District Health System Portal" /></title>
		
		<r:require module="survey"/>
	</head>
	<body>
		<div id="survey">
			<g:render template="/survey/header" model="[period: surveyPage.period, organisation: surveyPage.organisation]"/>
			
			<div class="grey-rounded-box-bottom">
				<div class="rounded-box-top rounded-box-bottom">
				<g:message code="survey.welcomemessage.label" default="Welcome to the survey, please fill in all the objectives above."/>
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</body>
</html>