<!-- Text type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-string ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<textarea name="surveyElements[${surveyElement.id}].value${suffix}" cols="${print? 130:100}" rows="${print? 12:8}" class="idle-field" ${readonly?'disabled="disabled"':''}>${value?.stringValue}</textarea>
	<g:if test="${lastValue!=null}"><span class="survey-old-value">(${lastValue})</span></g:if>
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>
