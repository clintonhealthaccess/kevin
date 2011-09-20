<%@ page import="org.apache.shiro.SecurityUtils" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="surveyPage.section.label" default="District Health System Portal" />
		</title>
	</head>
	<body>
		<g:set var="closed" value="${surveyPage.objectives[surveyPage.objective].closed}"/>
		<g:set var="readonly" value="${surveyPage.isReadonly(surveyPage.objective)}"/>
	
		<div id="survey">
			<g:render template="/survey/header" model="[period: surveyPage.period, organisation: surveyPage.organisation, objective: surveyPage.objective]"/>
			
			<div class="grey-rounded-box-bottom">

				<g:if test="${flash.message}">
					<div class="rounded-box-top rounded-box-bottom flash-info">
						<g:message code="${flash.message}" default="${flash.default}"/>
					</div>
				</g:if>
							
				<g:if test="${closed}">
					<div class="rounded-box-top rounded-box-bottom">
						This section has been submitted, you can view your answer here but you cannot change them.
					</div>
				</g:if>
				
<!-- 				<g:if test="${unavailable}"> -->
<!-- 					<div class="rounded-box-top rounded-box-bottom"> -->
<!-- 						This section can not yet be answered, please complete  -->
<!-- 						<a href="${createLink(controller: 'editSurvey', action: 'objectivePage', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.dependency.id])}"><g:i18n field="${surveyPage.objective.dependency.names}"/></a> -->
<!-- 						first. -->
<!-- 					</div> -->
<!-- 				</g:if>  -->
				
				<div class="rounded-box-top">
					<h5>
						<g:i18n field="${surveyPage.section.names}" />
					</h5>
				</div>
				<div class="rounded-box-bottom">
				
					<g:form id="survey-form" url="[controller:'editSurvey', action:'save', params: [organisation: surveyPage.organisation.id, section: surveyPage.section.id, survey: surveyPage.survey.id]]">
						<ol id="questions">
							<g:each in="${surveyPage.section.getQuestions(surveyPage.organisation.organisationUnitGroup)}" var="question">
								<li class="question-container ${surveyPage.questions[question].skipped?'skipped':''}">
									<g:render template="/survey/question/${question.getType()}" model="[surveyPage: surveyPage, question: question, readonly: readonly]" />
								</li> 
							</g:each>
						</ol>
						
						<g:if test="${!readonly}">
							<button type="submit">
								<g:if test="${surveyPage.isLastSection(surveyPage.section)}">
									Finish
								</g:if>
								<g:else>
									Next
								</g:else>
							</button>
						</g:if>
					</g:form>
				</div>
			</div>
		</div>
		<g:if test="${!readonly}">
		
			<script type="text/javascript">
				$(document).ready(function() {
					initializeSurvey(valueChangedInSection);
				});
			
				function valueChangedInSection(data, element) {
					
					// we go through all changed elements
					$.each(data.elements, function(index, element) {
						
						// we remove all the skips
						$('#element-'+element.id).find('.element').removeClass('skipped');
						
						// we add them again
						$.each(element.skipped, function(index, skipped) {
							$('#element-'+element.id).find('#element-'+element.id+'-'+escape(skipped)).addClass('skipped');
						});
						
						// we remove all the errors
						$('#element-'+element.id).find('.element').removeClass('errors');
						$('#element-'+element.id).find('.element').children('.error-list').html('');
						
						// we add them again
						$.each(element.invalid, function(index, invalid) {
							if (!invalid.valid) $('#element-'+element.id).find('#element-'+element.id+'-'+escape(invalid.prefix)).addClass('errors');
							$('#element-'+element.id).find('#element-'+element.id+'-'+escape(invalid.prefix)).children('.error-list').html(invalid.errors);
						});
						
					});
					
					// we go through all the questions
					$.each(data.questions, function(index, value) {
						if (value.skipped == true) $('#question-'+value.id).parents('.question-container').addClass('skipped')
						else $('#question-'+value.id).parents('.question-container').removeClass('skipped')
					});
				}
			</script>
		</g:if>
	</body>
</html>