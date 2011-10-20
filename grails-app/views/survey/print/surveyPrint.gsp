<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="print" />
	<title><g:message code="surveyPage.survey.label" default="District Health System Portal" /></title>
	
</head>
<body>
	<div>
		<div id="print-header">
			<div><g:message code="survey.print.district.health.system.label" default="District Health System"/></div> 
			<div><g:message code="survey.print.strengthening.framework.tool.label" default="Strengthening Framework Tool"/></div> 
			<div><g:message code="survey.print.questionnaire.label" default="Questionnaire"/></div>
			<div>${surveyPage.organisation.organisationUnitGroup.name}</div>
			<div>${surveyPage.organisation.organisationUnit.name}</div>
		</div>
		<div id="print-questions">
		<div>
			<g:each in="${surveyPage.survey.getObjectives(surveyPage.organisation.organisationUnitGroup)}" var="objective">
				<h3 class="objective-title"><g:i18n field="${objective.names}"/></h3>
				<g:each in="${objective.getSections(surveyPage.organisation.organisationUnitGroup)}" var="section">
					<h4 class="section-title"><g:i18n field="${section.names}"/></h4>
					<ol id="questions-section-${section.id}">
					<g:each in="${section.getQuestions(surveyPage.organisation.organisationUnitGroup)}" var="question">
						<li class="question-container">
							<g:render template="/survey/question/${question.getType()}" model="[surveyPage: surveyPage, question: question, readonly: readonly, print: true]" />
						</li>
					</g:each>
					</ol>
				</g:each>
			</g:each>
		</div>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
	</div>
</body>
</html>