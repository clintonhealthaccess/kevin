<!-- Bool type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-bool ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<input type="hidden" value="0" name="surveyElements[${surveyElement.id}].value${suffix}"/>
	<g:if test="${lastValue!=null}">
		<span class="survey-old-value">
			(
			<g:if test="${lastValue=='1'}">
				${"\u2611"}
			</g:if>
			<g:else>
				${"\u2610"}
			</g:else>
			)
		</span>
	</g:if>
	<input type="checkbox" value="1" name="surveyElements[${surveyElement.id}].value${suffix}" ${value?.booleanValue==true?'checked="checked"':''} ${readonly?'disabled="disabled"':''}/>
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>