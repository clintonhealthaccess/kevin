<!-- Enum type question -->
<div class="element element-enum element-${surveyElement.id} ${surveyEnteredValue?.skipped?'skipped':''} ${(surveyEnteredValue==null || surveyEnteredValue?.valid)?'':'errors'}" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
	
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
	<select name="surveyElements[${surveyElement.id}].value" ${readonly?'disabled="disabled"':''}>
		<option value="null">Select</option>
		<g:each in="${surveyElement.dataElement.enume?.enumOptions}" var="option">
			<option value="${option.value}"  ${option?.value==surveyEnteredValue?.value ? 'selected':''}>
				<g:i18n field="${option.names}" />
			</option>
		</g:each>
	</select>
	<g:if test="${surveyElementValue?.lastValue!=null}">
		<g:set var="option" value="${surveyElement.dataElement.enume.getOptionForValue(surveyElementValue.lastValue)}"/>
		<span class="survey-old-value">(${option!=null?i18n(field: option.names):surveyElementValue.lastValue})</span>
	</g:if>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>