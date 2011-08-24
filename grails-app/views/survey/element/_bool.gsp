<!-- Bool type question -->
<g:set var="surveyEnteredValue" value="${surveyPage.enteredValues[surveyElement]}"/>
<g:set var="surveyElementValue" value="${surveyPage.surveyElements[surveyElement.id]}"/>

<div class="element element-bool element-${surveyElement.id} ${surveyPage.isSkipped(surveyEnteredValue)?'skipped':''} ${!surveyPage.isValid(surveyEnteredValue)?'errors':''}" data-element="${surveyElement.id}" >
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
	
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id" />
	<input type="hidden" value="0" name="surveyElements[${surveyElement.id}].value"/>
	<input type="checkbox" value="1" name="surveyElements[${surveyElement.id}].value" ${surveyEnteredValue?.value=='1'?'checked="checked"':''} ${readonly?'disabled="disabled"':''}/>
	
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>