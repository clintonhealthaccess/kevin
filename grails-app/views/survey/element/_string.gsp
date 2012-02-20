<!-- Text type question -->
<div id="element-${element.id}-${suffix}" class="element element-string ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>

	<g:if test="${lastValue!=null}">
		<g:set var="tooltipValue" value="${lastValue.stringValue}" />
	</g:if>

	<input size="0" type="text" ${tooltipValue!=null?'title="'+tooltipValue+'"':''}
		value="${value?.stringValue}" name="elements[${element.id}].value${suffix}"
		class="${tooltipValue!=null?'tooltip':''} input idle-field ${!readonly?'loading-disabled':''}" disabled="disabled"/>

	<g:if test="${showHints}">
		<div class="admin-hint">Element: ${element.id} - Prefix: ${suffix}</div>
	</g:if>

	<div class="error-list">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>
</div>
