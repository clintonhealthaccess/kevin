<g:i18n field="${question.names}" />
<span>
	<g:set var="surveyElement" value="${question.surveyElement}"/> 
	<g:set var="dataElement" value="${surveyElement.dataElement}"/> 
	<g:render template="/survey/${dataElement.type}" model="[surveyElementValue: surveyElementValues[surveyElement.id]]" />
</span>

