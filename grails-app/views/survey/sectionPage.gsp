<%@ page import="org.apache.shiro.SecurityUtils" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.sectionPage.label" default="District Health System Portal" /></title>
		
		<r:require modules="survey"/> 
	</head>
	<body>
		<g:set var="closed" value="${surveyPage.enteredObjectives[surveyPage.objective].closed}"/>
		<g:set var="readonly" value="${surveyPage.isReadonly(surveyPage.objective)}"/>
	
		<div>
			<g:render template="/survey/header" model="[period: surveyPage.period, location: surveyPage.location, objective: surveyPage.objective]"/>
			
			<div class="main">

				<g:if test="${closed}">
					<div class="success-box">
						<p class="success">
							<g:message code="survey.section.submitted.text" default="This section has been submitted, you can view your answer here but you cannot change them."/>
						</p>
					</div>
				</g:if>
				<div>
					<h3 class="form-heading">
						<g:i18n field="${surveyPage.section.names}" />
					</h3>
				</div>
				
				<div id="js_survey">
					<g:form url="[controller:'editSurvey', action:'save', params: [location: surveyPage.location.id, section: surveyPage.section.id, survey: surveyPage.survey.id]]">
						<ol id="questions">
							<g:each in="${surveyPage.getQuestions(surveyPage.section)}" var="question" status="i">
								<li class="question-container ${surveyPage.enteredQuestions[question].skipped?'hidden':''} ${!surveyPage.enteredQuestions[question].complete?'incomplete':''} ${surveyPage.enteredQuestions[question].invalid?'invalid':''}">
									<g:render template="/survey/question/${question.getType().getTemplate()}" model="[surveyPage: surveyPage, question: question, readonly: readonly, questionNumber: i+1, showHints: SecurityUtils.subject.isPermitted('admin')]" />
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
	  						<li><a href="${createLink(controller:'editSurvey', action:'objectivePage', params:[objective: surveyPage.objective.id, location: surveyPage.location.id])}" class="go-back"><g:message code="survey.section.back.label"/></a></li>
	  					</ul>
					</g:form>
				</div>
			</div>
		</div>
		<r:script>
			$(document).ready(function() {
				${render(template:'/templates/messages')}
			
				new DataEntry({
					element: $('#js_survey'),
					callback: valueChangedInSection,
					url: "${createLink(controller:'editSurvey', action:'saveValue', params: [location: surveyPage.location.id, section: surveyPage.section?.id, objective: surveyPage.objective?.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});
			});
		
			function valueChangedInSection(dataEntry, data, element) {
				
				// we go through all the sections
				$.each(data.sections, function(index, section) {
					$('#section-'+section.id).find('.section-status').addClass('hidden');
					$('#section-'+section.id).find('.section-status-'+section.status).removeClass('hidden');
				});
				
				// we go through the objectives
				$.each(data.objectives, function(index, objective) {
					$('#objective-'+objective.id).find('.objective-status').addClass('hidden');
					$('#objective-'+objective.id).find('.objective-status-'+objective.status).removeClass('hidden');
				});
				
				// we go through all changed elements
				$.each(data.elements, function(index, element) {
					
					// we remove all the skips
					$('#element-'+element.id).find('.element').removeClass('skipped').find('.input').removeAttr('disabled');
					
					// we add them again
					$.each(element.skipped, function(index, skipped) {
						$('#element-'+element.id).find('#element-'+element.id+'-'+escape(skipped))
						.addClass('skipped').find('.input').attr('disabled', 'disabled');
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
					
					if (value.invalid == false) $('#question-'+value.id).parents('.question-container').removeClass('invalid')
					else $('#question-'+value.id).parents('.question-container').addClass('invalid')
				});
			}
		</r:script>
	</body>
</html>