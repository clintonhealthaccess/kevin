<%@ page import="org.chai.kevin.survey.validation.SurveyEnteredSection.SectionStatus" %>
<%@ page import="org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus" %>
<%@ page import="org.chai.kevin.survey.validation.SurveyEnteredQuestion.QuestionStatus" %>

<%@ page import="org.apache.shiro.SecurityUtils" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="surveyPage.section.label" default="District Health System Portal" />
		</title>
	</head>
	<body>
		<g:set var="closed" value="${surveyPage.objectives[surveyPage.objective].status == ObjectiveStatus.CLOSED}"/>
		<g:set var="unavailable" value="${surveyPage.objectives[surveyPage.objective].status == ObjectiveStatus.UNAVAILABLE}"/>
		<g:set var="canSave" value="${SecurityUtils.subject.isPermitted('survey:save')}"/>
		
		<g:set var="readonly" value="${closed||unavailable||!canSave}"/>
	
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
				
				<g:if test="${unavailable}">
					<div class="rounded-box-top rounded-box-bottom">
						This section can not yet be answered, please complete 
						<a href="${createLink(controller: 'editSurvey', action: 'objectivePage', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.dependency.id])}"><g:i18n field="${surveyPage.objective.dependency.names}"/></a>
						first.
					</div>
				</g:if> 
				
				<g:if test="${!closed && !unavailable}">
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
				</g:if>
			</div>
		</div>
		<g:if test="${!readonly}">
		
			<script type="text/javascript">
				$(document).ready(function() {
					$('#survey-form').delegate('input, select, textarea', 'change', function(){
						var element = $(this).parents('.element');
						surveyValueChanged(element, valueChangedInSection);
					});
					$('#survey-form').delegate('a.outlier-validation', 'click', function(){
						var element = $(this).parents('.element');
						surveyValueChanged(element, valueChangedInSection);						
						return false;
					});
					
					$('#survey-form').delegate('.element-list-add', 'click', function(){
						listAddClick(this, valueChangedInSection);
						return false;
					});
					$('#survey-form').delegate('.element-list-remove', 'click', function(){
						listRemoveClick(this, function(data, element) {
							$(element).parents('.question').addClass('errors');
							$(element).parents('.question-container').html(data.html);

							valueChangedInSection(data, element);
						});
						return false;
					});
				});
			
				function valueChangedInSection(data, element) {
					$('.question').each(function(key, question) {
						var valid = true;
						$(data.invalidQuestions).each(function(key, invalidQuestion) {
							if (invalidQuestion.id == $(question).data('question')) {
// 								$(question).parents('.question-container').html(invalidQuestion.html);
								valid = false;
							}
						});
						if (valid) {
							$(question).parents('.question-container').find('.error-list').remove()
							$(question).parents('.question-container').find('.errors').removeClass('errors');
						}
					});

					if (data.status == "invalid") {
						$(element).parents('.question').addClass('errors');
						$(element).parents('.question-container').html(data.html);
					}
						
					$('.question').each(function(key, element) {
						if ($.inArray($(element).data('question'), data.skippedQuestions) >= 0) {
							$(element).parents('.question-container').addClass('skipped');
						}
						else {
							$(element).parents('.question-container').removeClass('skipped');
						}
					});
					
					$('.element').each(function(key, element) {
						if ($.inArray($(element).data('element'), data.skippedElements) >= 0) {
							$(element).addClass('skipped');
						}
						else {
							$(element).removeClass('skipped');
						}
					});
				}
			</script>
		</g:if>
	</body>
</html>