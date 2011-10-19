<!-- Bool type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-bool ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:if test="${lastValue!=null}">
		<g:if test="${lastValue.booleanValue == true}">
			<g:set var="tooltipValue" value="${'Yes'}"/>
		</g:if>
		<g:if test="${lastValue.booleanValue == false}">
			<g:set var="tooltipValue" value="${'No'}"/>
		</g:if>
	</g:if>
		
	<g:if test="${isCheckbox}">
		<input class="input" type="hidden" value="0" name="surveyElements[${surveyElement.id}].value${suffix}"/>
		
		<input type="checkbox" ${tooltipValue!=null?'title="'+tooltipValue+'"':''} class="tooltip input ${!readonly?'loading-disabled':''}" value="1" name="surveyElements[${surveyElement.id}].value${suffix}" ${value?.booleanValue==true?'checked="checked"':''} disabled="disabled"/>
	</g:if>
	<g:else>
		<select class="tooltip input ${!readonly?'loading-disabled':''}" ${tooltipValue!=null?'title="'+tooltipValue+'"':''} name="surveyElements[${surveyElement.id}].value${suffix}" disabled="disabled">
			<option value="">Select</option>
			<option value="1" ${value?.booleanValue==true ? 'selected':''}>Yes</option>
			<option value="0" ${value?.booleanValue==false ? 'selected':''}>No</option>
		</select>
	</g:else>
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>
