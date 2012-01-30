<!-- Text type question -->
<div id="element-${element.id}-${suffix}" class="element element-text ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>

	<g:if test="${lastValue!=null}">
		<g:set var="tooltipValue" value="${lastValue.stringValue}" />
	</g:if>

	<textarea name="elements[${element.id}].value${suffix}"  ${tooltipValue!=null?'title="'+tooltipValue+'"':''}
			cols="${print? 130:100}" rows="${print? 12:4}" class="input idle-field ${tooltipValue!=null?'tooltip':''}" ${readonly?'disabled="disabled"':''}>${value?.stringValue}</textarea>

	<g:if test="${showHints}">
		<div class="admin-hint">Element: ${element.id} - Prefix: ${suffix}</div>
	</g:if>

	<div class="error-list">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>
</div>
