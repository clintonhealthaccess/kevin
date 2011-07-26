<!-- Text type question -->
<g:set var="surveyElement" value="${surveyElementValue.surveyElement}"/>
<g:set var="surveyEnteredValue" value="${surveyElementValue.surveyEnteredValue}"/>

<div class="element element-string" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
	<textarea name="surveyElements[${surveyElement.id}].value" cols="100" rows="8" class="idle-field" onchange="surveyValueChanged($(this).parents('.element').data('element'), this);"  ${readonly?'disabled="disabled"':''}>${surveyEnteredValue?.value}</textarea>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>
