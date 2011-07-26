<!-- Enum type question -->
<g:set var="surveyElement" value="${surveyElementValue.surveyElement}"/>
<g:set var="surveyEnteredValue" value="${surveyElementValue.surveyEnteredValue}"/>

<div class="element element-enum" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
	<select name="surveyElements[${surveyElement.id}].value" onchange="surveyValueChanged($(this).parents('.element').data('element'), this);" ${readonly?'disabled="disabled"':''}>
		<option value="null">Select</option>
		<g:each in="${surveyElement.dataElement.enume?.enumOptions}" var="option">
			<option value="${option.value}"  ${option?.value==surveyEnteredValue?.value ? 'selected':''}>
				<g:i18n field="${option?.names}" />
			</option>
		</g:each>
	</select>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>