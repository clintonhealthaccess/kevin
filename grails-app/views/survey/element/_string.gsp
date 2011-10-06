<!-- Text type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-string ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<input size="0" type="text" value="${value?.stringValue}" name="surveyElements[${surveyElement.id}].value${suffix}" class="input idle-field ${!readonly?'loading-disabled':''}" disabled="disabled"/>
	
	<g:if test="${lastValue!=null}"><span class="survey-old-value">(${lastValue.stringValue})</span></g:if>
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>
