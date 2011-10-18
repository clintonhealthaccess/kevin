<!-- Bool type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-bool ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:if test="${isCheckbox}">
		<input class="input" type="hidden" value="0" name="surveyElements[${surveyElement.id}].value${suffix}"/>
		<g:if test="${lastValue!=null}">
			<span class="survey-old-value">
				(
				<g:if test="${lastValue.booleanValue == true}">${"\u2611"}</g:if>
				<g:if test="${lastValue.booleanValue == false}">${"\u2610"}</g:if>
				)
			</span>
		</g:if>
		<input type="checkbox" class="input ${!readonly?'loading-disabled':''}" value="1" name="surveyElements[${surveyElement.id}].value${suffix}" ${value?.booleanValue==true?'checked="checked"':''} disabled="disabled"/>
	</g:if>
	<g:else>
		<select class="input ${!readonly?'loading-disabled':''}" name="surveyElements[${surveyElement.id}].value${suffix}" disabled="disabled">
			<option value="">Select</option>
			<option value="1" ${value?.booleanValue==true ? 'selected':''}>Yes</option>
			<option value="0" ${value?.booleanValue==false ? 'selected':''}>No</option>
		</select>
		<g:if test="${lastValue!=null}">
			(
			<g:if test="${lastValue.booleanValue == true}">Yes</g:if>
			<g:if test="${lastValue.booleanValue == false}">No</g:if>
			)
		</g:if>
	</g:else>
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>