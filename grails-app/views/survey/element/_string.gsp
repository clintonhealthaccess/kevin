<!-- Text type question -->
<g:set var="surveyEnteredValue" value="${surveyPage.enteredValues[surveyElement]}"/>
<g:set var="surveyElementValue" value="${surveyPage.surveyElements[surveyElement.id]}"/>

<div class="element element-string element-${surveyElement.id} ${surveyPage.isSkipped(surveyEnteredValue)?'skipped':''} ${!surveyPage.isValid(surveyEnteredValue)?'errors':''}" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
	
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
	<textarea name="surveyElements[${surveyElement.id}].value" cols="100" rows="8" class="idle-field" ${readonly?'disabled="disabled"':''}>${surveyEnteredValue?.value}</textarea>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>
