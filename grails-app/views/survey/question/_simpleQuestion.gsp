<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div class="question question-simple question-${question.id} ${surveyPage.isValid(question)?'':'errors'}" data-question="${question.id}">
	<g:i18n field="${question.names}" />
	<g:set var="surveyElement" value="${question.surveyElement}"/> 
	<g:set var="dataElement" value="${surveyElement.dataElement}"/>
	<g:set var="surveyEnteredValue" value="${surveyPage.enteredValues[surveyElement]}"/>
    <g:set var="surveyElementValue" value="${surveyPage.surveyElements[surveyElement.id]}"/>
		
	<g:render template="/survey/element/${dataElement.type}" model="[surveyElement: surveyElement, surveyElementValue: surveyElementValue, surveyEnteredValue: surveyEnteredValue, readonly: readonly, callback: callback]" />
</div>

