<!-- Bool type question -->
<div id="element-${element.id}-${suffix}" class="element element-bool ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>

	<g:if test="${lastValue!=null}">
		<g:if test="${lastValue.booleanValue == true}">
			<g:set var="tooltipValue" value="${message(code:'survey.element.bool.yes.label')}"/>
		</g:if>
		<g:if test="${lastValue.booleanValue == false}">
			<g:set var="tooltipValue" value="${message(code:'survey.element.bool.no.label')}"/>
		</g:if>
	</g:if>

	<g:if test="${isCheckbox}">
		<input class="input" type="hidden" value="0" name="elements[${element.id}].value${suffix}"/>
		<input type="checkbox" ${tooltipValue!=null?'title="'+tooltipValue+'"':''} class="${tooltipValue!=null?'tooltip':''} input ${!readonly?'loading-disabled':''}" value="1" name="elements[${element.id}].value${suffix}" ${value?.booleanValue==true?'checked="checked"':''} disabled="disabled"/>
	</g:if>
	<g:else>
		<g:if test="${!print}">
			<select class="${tooltipValue!=null?'tooltip':''} input ${!readonly?'loading-disabled':''}" ${tooltipValue!=null?'title="'+tooltipValue+'"':''} name="elements[${element.id}].value${suffix}" disabled="disabled">
				<option value=""><g:message code="survey.element.bool.select.label"/></option>
				<option value="1" ${value?.booleanValue==true ? 'selected':''}><g:message code="survey.element.bool.yes.label"/></option>
				<option value="0" ${value?.booleanValue==false ? 'selected':''}><g:message code="survey.element.bool.no.label"/></option>
			</select>
	    </g:if>
	    <g:else>
			<div class="yes-no-element">
				<input class="input" type="checkbox" value="1" name="option.names" disabled ${value?.booleanValue==true ? 'checked="checked" ':''}/><span><g:message code="survey.element.bool.yes.label"/></span>
			</div>
			<div class="yes-no-element">
			<input class="input" type="checkbox" value="0" name="option.names" disabled ${value?.booleanValue==false ? 'checked="checked" ':''}/><span><g:message code="survey.element.bool.no.label"/></span>
			</div>
		</g:else>
	</g:else>

	<g:if test="${showHints}">
		<div class="admin-hint">Element: ${element.id} - Prefix: ${suffix}</div>
	</g:if>

	<div class="error-list">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>
</div>
