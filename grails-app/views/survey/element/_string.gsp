<!-- Text type question -->
<div class="element element-string element-${surveyElement.id} ${surveyEnteredValue?.skipped?'skipped':''} ${(surveyEnteredValue==null || surveyEnteredValue?.valid)?'':'errors'}" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
	
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
	<textarea name="surveyElements[${surveyElement.id}].value" cols="100" rows="8" class="idle-field" ${readonly?'disabled="disabled"':''}>${surveyEnteredValue?.value}</textarea>
	<g:if test="${surveyElementValue?.lastValue!=null}"><span class="survey-old-value">(${surveyElementValue.lastValue})</span></g:if>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>
