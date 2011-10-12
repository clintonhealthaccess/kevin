<!-- Value type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-number ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>
	
	<input size="0" type="text" value="${formatNumber(number: value?.numberValue, format:'#')}" name="surveyElements[${surveyElement.id}].value${suffix}" class="idle-field input ${!readonly?'loading-disabled':''}" disabled="disabled"/>
	
	<g:if test="${lastValue!=null}"><span class="survey-old-value">(${formatNumber(number: lastValue?.numberValue, format:'#')})</span></g:if>
	
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>