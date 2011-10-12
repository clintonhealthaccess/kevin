<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div id="question-${question.id}" class="question question-simple" data-question="${question.id}">
	<g:i18n field="${question.names}" />
	
	<g:set var="surveyElement" value="${question.surveyElement}"/>
	<g:set var="dataElement" value="${surveyElement.dataElement}"/>
	<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />

	<g:if test="${print && dataElement.type.type.name().toLowerCase()=='list' && !appendix}">
		<h4>--- <g:message code="survey.print.see.appendix" default="See Appendix"/> ---</h4>
	</g:if>
	<g:else>
		<div id="element-${surveyElement.id}" class="survey-element">
			<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}"  model="[
				value: enteredValue.value,
				lastValue: enteredValue.lastValue,
				type: dataElement.type, 
				suffix:'',
				surveyElement: surveyElement, 
				enteredValue: enteredValue, 
				readonly: readonly,
				print: print,
				appendix: appendix
			]"/>
		</div>
	</g:else>
</div>

