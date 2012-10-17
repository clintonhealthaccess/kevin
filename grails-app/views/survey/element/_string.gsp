<!-- Text type question -->
<div id="element-${element.id}-${suffix}" class="element element-string ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>

	<input size="0" type="text"
		value="${value?.stringValue}" name="elements[${element.id}].value${suffix}"
		class="input idle-field ${!readonly?'loading-disabled':''}" disabled="disabled"/>

	<g:if test="${lastValue!=null && !lastValue.null}">
		<g:set var="tooltipValue" value="${lastValue.stringValue}" />
		
		<g:render template="/templates/help_tooltip" model="[names: tooltipValue]" />
	</g:if>

	<g:render template="/survey/element/hints"/>

	<div class="error-list">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>
</div>
