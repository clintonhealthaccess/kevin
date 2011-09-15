<g:set var="enteredQuestion" value="${surveyPage.questions[question]}"/>
<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div id="question-${question.id}" class="question question-simple" data-question="${question.id}">
	<g:i18n field="${question.names}" />
	
	<g:set var="surveyElement" value="${question.surveyElement}"/>
	<g:set var="dataElement" value="${surveyElement.dataElement}"/>
					
	<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />

	<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>

	<div id="element-${surveyElement.id}">
		<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}"  model="[
			value: enteredValue.value,
			lastValue: enteredValue.lastValue,
			type: dataElement.type, 
			suffix:'',
			surveyElement: surveyElement, 
			enteredValue: enteredValue, 
			readonly: readonly
		]"/>
	</div>
</div>

