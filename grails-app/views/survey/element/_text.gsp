<!-- Text type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-text ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:if test="${lastValue!=null}">
		<g:set var="tooltipValue" value="${lastValue.stringValue}" />
	</g:if>
	
	<textarea
			name="surveyElements[${surveyElement.id}].value${suffix}"  ${tooltipValue!=null?'title="'+tooltipValue+'"':''} 
			cols="${print? 130:100}" rows="${print? 12:4}" class="idle-field tooltip" ${readonly?'disabled="disabled"':''}>${value?.stringValue}</textarea>

	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>
