<!-- Value type question -->
<g:set var="surveyEnteredValue" value="${surveyPage.enteredValues[surveyElement]}"/>
<g:set var="surveyElementValue" value="${surveyPage.surveyElements[surveyElement.id]}"/>

<div class="element element-value element-${surveyElement.id} ${surveyPage.isSkipped(surveyEnteredValue)?'skipped':''} ${!surveyPage.isValid(surveyEnteredValue)?'errors':''}" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
	
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
	<input size="0" type="text" value="${surveyEnteredValue?.value}" name="surveyElements[${surveyElement.id}].value" class="idle-field" ${readonly?'disabled="disabled"':''}/>
	<g:if test="${surveyElementValue.lastValue!=null}"><span class="survey-old-value">(${surveyElementValue.lastValue})</span></g:if>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>