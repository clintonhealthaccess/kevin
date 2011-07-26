<!-- Date type question -->
<g:set var="surveyElement" value="${surveyElementValue.surveyElement}"/>
<g:set var="surveyEnteredValue" value="${surveyElementValue.surveyEnteredValue}"/>

<div class="element element-date" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
	<input type="text" value="${surveyEnteredValue?.value}" name="surveyElements[${surveyElement.id}].value" class="idle-field"  onchange="surveyValueChanged($(this).parents('.element').data('element'), this);" ${readonly?'disabled="disabled"':''}/>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>