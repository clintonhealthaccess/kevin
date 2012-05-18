<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="print" />
	<title><g:message code="survey.print.title" /></title>
	
</head>
<body>
	<div>
		<div id="print-header">
			<div><g:message code="survey.print.district.health.system.label"/></div> 
			<div><g:message code="survey.print.strengthening.framework.tool.label"/></div> 
			<div><g:message code="survey.print.questionnaire.label"/></div>
			<div><g:i18n field="${surveyPage.dataLocation.type.names}"/></div>
			<div><g:i18n field="${surveyPage.dataLocation.names}"/></div>
		</div>
		<div id="print-questions">
		<div>
			<g:each in="${surveyPage.survey.getPrograms(surveyPage.dataLocation.type)}" var="program">
				<h3 class="program-title"><g:i18n field="${program.names}"/></h3>
				<g:each in="${program.getSections(surveyPage.dataLocation.type)}" var="section">
					<h4 class="section-title"><g:i18n field="${section.names}"/></h4>
					<ol id="questions-section-${section.id}">
					<g:each in="${surveyPage.getQuestions(section)}" var="question">
						<li class="question-container">
							<g:render template="/survey/question/${question.getType().getTemplate()}" model="[surveyPage: surveyPage, question: question, readonly: readonly, print: true, showHints: false]" />
						</li>
					</g:each>
					</ol>
				</g:each>
			</g:each>
		</div>
		<div class="appendix-content">
			<h3 class="appendix-title"><g:message code="survey.print.appendix"/></h3>
			<ol>
				<g:each in="${surveyPage.getListQuestions(surveyPage.survey)}" var="question">
					<li class="question-container">
					<div>
					   <h4><g:i18n field="${question.section.program.names}"/> &rarr; 
					   <g:i18n field="${question.section.names}"/></h4>
					</div>
					<g:render template="/survey/question/${question.getType().getTemplate()}" model="[surveyPage: surveyPage, question: question, readonly: readonly, print: true, appendix: true, showHints: false]" />
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