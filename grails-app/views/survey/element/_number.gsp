<!-- Value type question -->
<div id="element-${element.id}-${suffix}" class="element element-number ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">

	<a name="element-${element.id}-${suffix}"></a>

	<input size="0" type="text"
		onkeydown="return (event.ctrlKey || event.altKey || (47<event.keyCode && event.keyCode<58 && event.shiftKey==false) || (95<event.keyCode && event.keyCode<106) || (event.keyCode==8) || (event.keyCode==9) || (event.keyCode>34 && event.keyCode<40) || (event.keyCode==46) )"
		value="${formatNumber(number: value?.numberValue, format:'#')}" name="elements[${element.id}].value${suffix}" class="idle-field input ${!readonly?'loading-disabled':''}" disabled="disabled"/>
		
	<g:if test="${lastValue!=null && !lastValue.null}">
		<g:set var="tooltipValue" value="${formatNumber(number: lastValue?.numberValue, format:'#')}"/>
		
		<g:render template="/templates/help_tooltip" model="[names: tooltipValue]" />
	</g:if>
		

	<g:render template="/survey/element/hints"/>

	<div class="error-list">
		<g:renderUserErrors  element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>
	
</div>
