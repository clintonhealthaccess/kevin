<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="surveyPage.survey.label" default="District Health System Portal" />
		</title>
	</head>
	<body>
		<div id="survey">
			<g:render template="/survey/header" model="[period: surveyPage.period, organisation: surveyPage.organisation]"/>
			
			<div id="bottom-container">
				<g:render template="/survey/menu" model="[surveyPage: surveyPage]"/>
				
				<div id="survey-right-question-container" class="grey-rounded-box-bottom">
					<div class="rounded-box-top rounded-box-bottom">
						Welcome to the survey, please fill in all the objectives on the left.
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</div>
	</body>
</html>