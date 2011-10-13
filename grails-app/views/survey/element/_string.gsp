<!-- Text type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-string ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:if test="${lastValue!=null}">
		<g:set var="tooltipValue" value="${lastValue.stringValue}" />
	</g:if>

	<input 	size="0" type="text" ${tooltipValue!=null?'title="'+tooltipValue+'"':''} 
			value="${value?.stringValue}" name="surveyElements[${surveyElement.id}].value${suffix}" 
			class="tooltip input idle-field ${!readonly?'loading-disabled':''}" disabled="disabled"/>
	
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>
