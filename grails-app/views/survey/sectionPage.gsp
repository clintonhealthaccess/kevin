<%@ page import="org.apache.shiro.SecurityUtils" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.sectionPage.label" default="District Health System Portal" /></title>
		
		<r:require modules="survey,datepicker"/> 
	</head>
	<body>
		<g:set var="closed" value="${surveyPage.objectives[surveyPage.objective].closed}"/>
		<g:set var="readonly" value="${surveyPage.isReadonly(surveyPage.objective)}"/>
	
		<div id="survey">
			<g:render template="/survey/header" model="[period: surveyPage.period, organisation: surveyPage.organisation, objective: surveyPage.objective]"/>
			
			<div class="main">

				<g:if test="${flash.message != null}">
					<div class="message">${flash.message}</div>
				</g:if>
							
				<g:if test="${closed}">
					<div>
						<g:message code="survey.section.submitted.text" default="This section has been submitted, you can view your answer here but you cannot change them."/>
					</div>
				</g:if>
				
				<div>
					<h3 class="form-heading">
						<g:i18n field="${surveyPage.section.names}" />
					</h3>
				</div>
				
				<div>
					<g:form id="survey-form" url="[controller:'editSurvey', action:'save', params: [organisation: surveyPage.organisation.id, section: surveyPage.section.id, survey: surveyPage.survey.id]]">
						<ol id="questions">
							<g:each in="${surveyPage.section.getQuestions(surveyPage.organisation.organisationUnitGroup)}" var="question" status="i">
								<li class="question-container ${surveyPage.questions[question].skipped?'hidden':''} ${!surveyPage.questions[question].complete?'incomplete':''}">
									<g:render template="/survey/question/${question.getType().getTemplate()}" model="[surveyPage: surveyPage, question: question, readonly: readonly, questionNumber: i+1]" />
								</li> 
							</g:each>
						</ol>
						
						<ul class="form-actions">
							<g:if test="${!readonly}">
								<li>
									<button type="submit" class="loading-disabled">
										<g:if test="${surveyPage.isLastSection(surveyPage.section)}">
											<g:message code="survey.section.finish.label" default="Finish"/>
										</g:if>
										<g:else>
											<g:message code="survey.section.next.label" default="Next"/>
										</g:else>
									</button>
								</li>
								<li>
									<button type="cancel" class="hidden">
										<g:message code="survey.section.cancel.label" default="Cancel"/>
									</button>
								</li>
							</g:if>
	  						<li><a href="#" class="go-back"><g:message code="survey.section.back.label" default="Go back"/></a></li>
	  					</ul>
					</g:form>
				</div>
			</div>
		</div>
		<g:if test="${!readonly}">
			<r:script>
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
						if (value.skipped == true) $('#question-'+value.id).parents('.question-container').addClass('hidden')
						else $('#question-'+value.id).parents('.question-container').removeClass('hidden')
						
						if (value.complete == true) $('#question-'+value.id).parents('.question-container').removeClass('incomplete')
						else $('#question-'+value.id).parents('.question-container').addClass('incomplete')
					});
				}
			</r:script>
		</g:if>
	</body>
</html>