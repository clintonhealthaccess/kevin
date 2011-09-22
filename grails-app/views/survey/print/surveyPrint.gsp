<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="mainprint" />
<title><g:message code="surveyPage.survey.label" default="District Health System Portal" /></title>
</head>
<body>
	<div>
		<div id="print-header">
			<span class="display-in-block"><g:message code="survey.print.district.health.system.label" default="District Health System"/></span> 
			<span  class="display-in-block"><g:message code="survey.print.strengthening.framework.tool.label" default="Strengthening Framework Tool"/></span> 
			<span  class="display-in-block"><g:message code="survey.print.questionnaire.label" default="Questionnaire"/></span>
				<span  class="display-in-block">${surveyPage.organisation.organisationUnitGroup.name}</span>
				<span  class="display-in-block">${surveyPage.organisation.organisationUnit.name}</span>
		</div>
		<div id="print-questions">
		<div>
			<g:each in="${surveyPage.survey.getObjectives(organisationUnitGroup)}" var="objective">
				<h3 class="objective-title"><g:i18n field="${objective.names}"/></h3>
				<g:each in="${objective.getSections(organisationUnitGroup)}" var="section">
					<h4 class="section-title"><g:i18n field="${section.names}"/></h4>
					<ol id="questions-section-${section.id}">
					<g:each in="${section.getQuestions(organisationUnitGroup)}" var="question">
						<li class="question-container">
						<g:render template="/survey/question/${question.getType()}" model="[surveyPage: surveyPage, question: question, readonly: readonly, print: true]" />
						</li>
					</g:each>
					</ol>
				</g:each>
			</g:each>
		</div>
			<div>
				<h3 class="appendix-title"><g:message code="survey.print.appendix" default="Appendix"/></h3>
				<ol>
					<g:each in="${surveyPage.getListQuestions(surveyPage.survey)}" var="question">
						<li class="question-container">
						<span class="display-in-block">
						   <h4><g:i18n field="${question.section.objective.names}"/> &rarr; 
						   <g:i18n field="${question.section.names}"/></h4>
						</span>
						<g:render template="/survey/question/${question.getType()}" model="[surveyPage: surveyPage, question: question, readonly: readonly, print: true, appendix: true]" />
						</li>
					</g:each>
				</ol>
			</div>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
	</div>

</body>
</html>