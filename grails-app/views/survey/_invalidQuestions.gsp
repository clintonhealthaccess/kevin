<%@ page import="org.apache.shiro.SecurityUtils" %>
<g:set value="${surveyPage.getInvalidQuestions(surveyPage.objective)}" var="invalidQuestions"/>
<g:if test="${!invalidQuestions.isEmpty()}">
	<div><g:message code="survey.objective.error.text" default="The following questions do not pass validation, please check" />:</div>
	<form id="survey-form">
		<g:each in="${invalidQuestions}" var="question" status="i">
			<div class="invalid-question ${i!=0?'hidden':''}">
				<h5>In section: <g:i18n field="${question.section.names}" /> </h5>
				<div class="question-container" class="invalid ${!surveyPage.enteredQuestions[question].complete?'incomplete':''}">
					<!-- separation -->
					<g:render template="/survey/question/${question.getType().getTemplate()}" model="[question: question, surveyPage: surveyPage, readonly: surveyPage.isReadonly(surveyPage.objective), showHints: SecurityUtils.subject.isPermitted('admin')]" />
				</div> 
				<g:if test="${i!=0}">
					<a href="#" onclick="$(this).parents('.invalid-question').hide();$(this).parents('.invalid-question').prev().show();"><g:message code="survey.invalid.previous.label" default="Previous" /></a>
				</g:if>
				<g:if test="${i!=invalidQuestions.size()-1}">
					<a href="#" onclick="$(this).parents('.invalid-question').hide();$(this).parents('.invalid-question').next().show();"><g:message code="survey.invalid.next.label" default="Next" /></a>
				</g:if>
			</div>
		</g:each>
	</form>
</g:if>