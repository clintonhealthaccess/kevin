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
				
				<g:if test="${flash.message}">
					<div class="rounded-box-top rounded-box-bottom flash-info">
						<g:message code="${flash.message}" default="${flash.default}"/>
					</div>
				</g:if>
				
				<g:if test="${closed}">
					<div class="rounded-box-top rounded-box-bottom">
						This objective has been already been submitted. Please go on with the other sections.
						<shiro:hasPermission permission="admin:survey">
							<a href="${createLink(controller: 'editSurvey', action: 'reopen', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id])}">Reopen this objective.</a>
						</shiro:hasPermission>
					</div>
				</g:if>
<!-- 				<g:if test="${unavailable}"> -->
<!-- 					<div class="rounded-box-top rounded-box-bottom"> -->
<!-- 						This objective can not yet be answered, please complete  -->
<!-- 						<a href="${createLink(controller: 'editSurvey', action: 'objectivePage', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.dependency.id])}"> -->
<!-- 							<g:i18n field="${surveyPage.objective.dependency.names}"/> -->
<!-- 						</a> -->
<!-- 						first. -->
<!-- 					</div> -->
<!-- 				</g:if> -->
				
				<g:if test="${!closed}">
					<div id="submit-objective" class="${!surveyPage.canSubmit(surveyPage.objective)?'hidden':''} rounded-box-top rounded-box-bottom">
						This part has been completed successfully. If you are sure that you entered the right data, please click submit.
						<g:form url="[controller:'editSurvey', action:'submit', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id]]">
							<button type="submit">Submit</button>
						</g:form>
					</div>

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
					else $('#submit-objective').addClass('hidden')
				}
			</r:script>
		</g:if>
	</body>
</html>