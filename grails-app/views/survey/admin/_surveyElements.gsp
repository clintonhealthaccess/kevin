<g:each in="${surveyElements}" status="i" var="surveyElement">
<g:set var="question" value="${surveyElement.surveyQuestion}"/>
	<li data-code="${surveyElement.id}" id="survey-element-${surveyElement.id}">
		<a	class="no-link cluetip" onclick="return false;" title="${i18n(field:surveyElement.dataElement.names)}"
			href="${createLink(controller:'surveyElement', action:'getDescription', params:[surveyElement: surveyElement.id])}"
			rel="${createLink(controller:'surveyElement', action:'getDescription', params:[surveyElement: surveyElement.id])}">
			<g:i18n field="${question.section.objective.survey.names}" /> |
			<g:i18n field="${question.section.objective.names}" /> |
			<g:i18n field="${question.section.names}" /> |	
		<g:i18n field="${surveyElement.dataElement.names}"/></a> 
		<span>[${surveyElement.id}]</span>
	</li>
</g:each>
<g:if test="${surveyElements.isEmpty()}">
No match
</g:if>

