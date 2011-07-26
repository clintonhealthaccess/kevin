<div>
	<g:i18n field="${question.names}" />
	<g:set var="surveyElement" value="${question.surveyElement}"/> 
	<g:set var="dataElement" value="${surveyElement.dataElement}"/>
	
	<g:render template="/survey/element/${dataElement.type}" model="[surveyElementValue: surveyElementValues[surveyElement.id], readonly: readonly]" />
</div>

