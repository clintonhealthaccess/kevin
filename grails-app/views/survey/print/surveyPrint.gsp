<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="mainprint" />
<title><g:message code="surveyPage.survey.label" default="District Health System Portal" /></title>
</head>
<body>
	<div>
		<div>
			<g:each in="${objectives}" var="objective">
				<h3 class="objective-title"><g:i18n field="${objective.names}"/></h3>
				<g:each in="${objective.sections}" var="section">
					<h4 class="section-title"><g:i18n field="${section.names}"/></h4>
					<g:each in="${section.questions}" var="question">
						<div class="question-container">
						${question.order})
						
						</div>
					</g:each>
				</g:each>

			</g:each>
		</div>
		<div class="clear"></div>
	</div>

</body>
</html>