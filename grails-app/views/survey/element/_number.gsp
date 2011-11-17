<!-- Value type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-number ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:if test="${lastValue!=null}">
		<g:set var="tooltipValue" value="${formatNumber(number: lastValue?.numberValue, format:'#')}"/>
	</g:if>

	<input size="0" type="text" ${tooltipValue!=null?'title="'+tooltipValue+'"':''}
		onkeydown="return (event.ctrlKey || event.altKey || (47<event.keyCode && event.keyCode<58 && event.shiftKey==false) || (95<event.keyCode && event.keyCode<106) || (event.keyCode==8) || (event.keyCode==9) || (event.keyCode>34 && event.keyCode<40) || (event.keyCode==46) )"
		value="${formatNumber(number: value?.numberValue, format:'#')}" name="surveyElements[${surveyElement.id}].value${suffix}" class="${tooltipValue!=null?'tooltip':''} idle-field input ${!readonly?'loading-disabled':''}" disabled="disabled"/>

	<shiro:hasPermission permission="admin">
		<div class="admin-hint">Element: ${surveyElement.id} - Prefix: ${suffix}</div>
	</shiro:hasPermission>

	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>
