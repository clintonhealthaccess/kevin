<g:if test="${type.enumCode != null}">
	<g:set var="enume" value="${enums?.get(type.enumCode)}"/>
</g:if>

<!-- Enum type question -->
<div id="element-${element.id}-${suffix}" class="element element-enum ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>
   	<g:if test="${!print}">
	   	<g:if test="${lastValue!=null}">
			<g:set var="option" value="${enume?.getOptionForValue(lastValue.enumValue)}"/>
			<g:set var="tooltipValue" value="${option!=null?i18n(field: option.names):lastValue.enumValue}"/>
		</g:if>

		<select class="${tooltipValue!=null?'tooltip':''} input ${!readonly?'loading-disabled':''}" ${tooltipValue!=null?'title="'+tooltipValue+'"':''} name="elements[${element.id}].value${suffix}" disabled="disabled">
			<option value=""><g:message code="survey.element.enum.select.label"/></option>
		
			<g:eachOption enum="${enume}" var="option">
				<option value="${option.value}" ${option?.value==value?.enumValue ? 'selected':''}>
					<g:i18n field="${option.names}" />
				</option>
			</g:eachOption>
		</select>
	</g:if>
	<g:else>
	<label>-- <g:message code="survey.print.selectonlyoneoption.label" default="Select only one response"/> --</label>
		<g:eachOption enum="${enume}" var="option">
			<div>
				<input class="input" type="checkbox" value="1" name="option.names" ${option?.value==value?.enumValue? 'checked="checked" ':''} disabled="disabled"/>
				<span><g:i18n field="${option.names}" /></span>
			</div>
		</g:eachOption>
	</g:else>
	
	<g:if test="${showHints}">
		<div class="admin-hint">Element: ${element.id} - Prefix: ${suffix}</div>
	</g:if>
	
	<div class="error-list">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>
</div>
