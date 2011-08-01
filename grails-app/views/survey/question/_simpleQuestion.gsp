<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div class="question question-simple question-${question.id} ${surveyPage.isValid(question)?'':'errors'}" data-question="${question.id}">
	<g:i18n field="${question.names}" />
	<g:set var="surveyElement" value="${question.surveyElement}"/> 
	<g:set var="dataElement" value="${surveyElement.dataElement}"/>
	
	<g:render template="/survey/element/${dataElement.type}" model="[surveyElement: surveyElement, surveyPage: surveyPage, readonly: readonly, callback: callback]" />
</div>

