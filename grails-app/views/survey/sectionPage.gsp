<%@ page import="org.apache.shiro.SecurityUtils" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.section.title" /></title>
		
		<r:require modules="survey"/> 
	</head>
	<body>
		<g:set var="closed" value="${surveyPage.enteredPrograms[surveyPage.program].closed}"/>
		<g:set var="readonly" value="${surveyPage.isReadonly(surveyPage.program)}"/>
	
		<div>
			<g:render template="/survey/header" model="[period: surveyPage.period, location: surveyPage.location, program: surveyPage.program]"/>
			
			<div class="main">

				<g:if test="${closed}">
					<div class="success-box">
						<p class="success">
							<g:message code="survey.section.submitted.text"/>
						</p>
					</div>
				</g:if>
				<div>
					<h3 class="heading1">
						<g:i18n field="${surveyPage.section.names}" />
					</h3>
				</div>
				<div id="js_survey">
					<g:form url="[controller:'editSurvey', action:'save', params: [location: surveyPage.location.id, section: surveyPage.section.id, survey: surveyPage.survey.id]]">
					
						<ol>
							<g:each in="${surveyPage.getQuestions(surveyPage.section)}" var="question" status="i">
								<li class="question-container">
									<g:render template="/survey/question/${question.getType().getTemplate()}" model="[surveyPage: surveyPage, question: question, readonly: readonly, questionNumber: i+1, showHints: SecurityUtils.subject.isPermitted('admin')]" />
								</li> 
							</g:each>
						</ol>
						
						<ul class="form-actions">
							<g:if test="${!readonly}">
								<li>
									<button type="submit" class="loading-disabled">
										<g:if test="${surveyPage.isLastSection(surveyPage.section)}">
											<g:message code="survey.section.finish.label"/>
										</g:if>
										<g:else>
											<g:message code="survey.section.next.label"/>
										</g:else>
									</button>
								</li>
								<li>
									<button type="cancel" class="hidden">
										<g:message code="survey.section.cancel.label"/>
									</button>
								</li>
							</g:if>
	  						<li><a href="${createLink(controller:'editSurvey', action:'programPage', params:[program: surveyPage.program.id, location: surveyPage.location.id])}" class="go-back"><g:message code="survey.section.back.label"/></a></li>
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
					url: "${createLink(controller:'editSurvey', action:'saveValue', params: [location: surveyPage.location.id, section: surveyPage.section?.id, program: surveyPage.program?.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});
			});
		
			function valueChangedInSection(dataEntry, data, element) {
				if (data.status == 'success') {
					// we go through all the sections
					$.each(data.sections, function(index, section) {
						$('#section-'+section.id).find('.section-status').addClass('hidden');
						$('#section-'+section.id).find('.section-status-'+section.status).removeClass('hidden');
					});
					
					// we go through the programs
					$.each(data.programs, function(index, program) {
						$('#program-'+program.id).find('.program-status').addClass('hidden');
						$('#program-'+program.id).find('.program-status-'+program.status).removeClass('hidden');
					});
					
					// we go through all the questions
					$.each(data.questions, function(index, value) {
						if (value.skipped == true) $('#question-'+value.id).parents('.question-container').addClass('hidden')
						else $('#question-'+value.id).parents('.question-container').removeClass('hidden')
						
						if (value.complete == true) $('#question-'+value.id).removeClass('incomplete')
						else $('#question-'+value.id).addClass('incomplete')
						
						if (value.invalid == false) $('#question-'+value.id).removeClass('invalid')
						else $('#question-'+value.id).addClass('invalid')
					});
				}
				else {
					alert(self.settings.messages['dataentry.saving.program.closed.text']);
				}
			}
		</r:script>
	</body>
</html>