<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.objectivePage.label" default="District Health System Portal" /></title>
		
		<r:require modules="survey,datepicker"/>
	</head>
	<body>
		<div id="survey">
			<g:render template="/survey/header" model="[period: surveyPage.period, organisation: surveyPage.organisation, objective: surveyPage.objective]"/>
			
			<div class="main">
				<g:set value="${surveyPage.objectives[surveyPage.objective].closed}" var="closed"/>
				<g:set var="readonly" value="${surveyPage.isReadonly(surveyPage.objective)}"/>
				
				<g:if test="${closed}">
					<div>
						<g:message code="survey.objective.submitted.text" default="This objective has already been submitted, please go on with the other sections." />
						<shiro:hasPermission permission="admin:survey">
							<a href="${createLink(controller: 'editSurvey', action: 'reopen', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id])}">
								<g:message code="survey.objective.reopen.text"/>
							</a>
						</shiro:hasPermission>
					</div>
				</g:if>
				
				<g:if test="${!closed}">
					<g:if test="${surveyPage.canSubmit(surveyPage.objective)}">
						<div id="submit-objective" class="${!surveyPage.canSubmit(surveyPage.objective)?'hidden':''} success-box">
							<p class="success"><g:message code="survey.objective.ready.text" default="This part has been completed successfully. If you are sure that you entered the right data, please click submit." /></p>
							<g:form url="[controller:'editSurvey', action:'submit', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id]]">
								<button type="submit">Submit</button>
							</g:form>
						</div>
					</g:if>

					<div id="incomplete-sections-container">
						<g:render template="/survey/incompleteSections" model="[surveyPage: surveyPage]" />
					</div>
				
					<div id="invalid-questions-container">
						<g:render template="/survey/invalidQuestions" model="[surveyPage: surveyPage]" />
					</div>
				</g:if>
			</div>
		</div>
		<g:if test="${!readonly}">
			<r:script>
				$(document).ready(function() {
					initializeSurvey(valueChangedInObjective);
				});
			
				function valueChangedInObjective(data, element) {
					$('#incomplete-sections-container').html(data.incompleteSections);
					$('#invalid-questions-container').html(data.invalidQuestions);
					
					if ($.trim(data.invalidQuestions) == '' && $.trim(data.incompleteSections) == '') $('#submit-objective').removeClass('hidden');
					else $('#submit-objective').addClass('hidden');
					
					$('.loading-disabled').removeClass('loading-disabled').removeAttr('disabled');
				}
			</r:script>
		</g:if>
	</body>
</html>