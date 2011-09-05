<!-- Bool type question -->
<div class="element element-bool element-${surveyElement.id} ${surveyEnteredValue?.skipped?'skipped':''} ${(surveyEnteredValue==null || surveyEnteredValue?.valid)?'':'errors'}" data-element="${surveyElement.id}" >
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
	
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id" />
	<input type="hidden" value="0" name="surveyElements[${surveyElement.id}].value"/>
	<g:if test="${surveyElementValue?.lastValue!=null}">
		<span class="survey-old-value">
			(
			<g:if test="${surveyElementValue.lastValue=='1'}">
				${"\u2611"}
			</g:if>
			<g:else>
				${"\u2610"}
			</g:else>
			)
		</span>
	</g:if>
	<input type="checkbox" value="1" name="surveyElements[${surveyElement.id}].value" ${surveyEnteredValue?.value=='1'?'checked="checked"':''} ${readonly?'disabled="disabled"':''}/>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>