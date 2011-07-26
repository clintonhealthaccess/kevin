<!-- Bool type question -->
<g:set var="surveyElement" value="${surveyElementValue.surveyElement}"/>
<g:set var="surveyEnteredValue" value="${surveyElementValue.surveyEnteredValue}"/>

<div class="element element-bool" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id" />
	<input type="hidden" value="0" name="surveyElements[${surveyElement.id}].value"/>
	<input type="checkbox" value="1" name="surveyElements[${surveyElement.id}].value" ${surveyEnteredValue?.value=='1'?'checked="checked"':''} onchange="surveyValueChanged($(this).parents('.element').data('element'), this);" ${readonly?'disabled="disabled"':''}/>
	
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>