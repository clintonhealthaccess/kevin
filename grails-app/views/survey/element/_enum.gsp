<g:if test="${type.enumCode != null}">
	<g:set var="enume" value="${enums?.get(type.enumCode)}"/>
</g:if>

<!-- Enum type question -->
<div id="element-${element.id}-${suffix}" class="element element-enum ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>
   	<g:if test="${!print}">

		<select class="input ${!readonly?'loading-disabled':''}" name="elements[${element.id}].value${suffix}" disabled="disabled" data-enum="${enume?.id}">
			<option value=""><g:message code="survey.element.enum.select.label"/></option>
		
			<g:eachOption enum="${enume}" var="option">
				<option value="${option.value}" ${option?.value==value?.enumValue ? 'selected':''} data-option="${option.id}">
					<g:i18n field="${option.names}" />
				</option>
			</g:eachOption>
		</select>

	   	<g:if test="${lastValue!=null && !lastValue.null}">
			<g:set var="option" value="${enume?.getOptionForValue(lastValue.enumValue)}"/>
			<g:set var="tooltipValue" value="${option!=null?i18n(field: option.names):lastValue.enumValue}"/>
			
			<g:render template="/templates/help_tooltip" model="[names: tooltipValue]" />
		</g:if>

		<div class="option-description-container">		
			<g:eachOption enum="${enume}" var="option">
				<div id="enum-${enume.id}-option-${option.id}" class="option-description hidden">
					<g:stripHtml field="${i18n(field: option.descriptions)}" chars="40"/>
				</div>
			</g:eachOption>
		</div>
	</g:if>
	<g:else>
	<label>-- <g:message code="survey.print.selectonlyoneoption.label"/> --</label>
		<g:eachOption enum="${enume}" var="option">
			<div>
				<input class="input" type="checkbox" value="1" name="option.names" ${option?.value==value?.enumValue? 'checked="checked" ':''} disabled="disabled"/>
				<span><g:i18n field="${option.names}" /></span>
			</div>
		</g:eachOption>
	</g:else>
	
	<g:render template="/survey/element/hints"/>
	
	<div class="error-list">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>
</div>
