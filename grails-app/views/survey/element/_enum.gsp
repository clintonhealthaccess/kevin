<%@ page import="org.chai.kevin.data.Enum" %>
<g:if test="${type.enumCode != null}">
	<g:set var="enume" value="${Enum.findByCode(type.enumCode)}"/>
</g:if>

<!-- Enum type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-enum ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>
   <g:if test="${!print}">
	<select name="surveyElements[${surveyElement.id}].value${suffix}" ${readonly?'disabled="disabled"':''}>
		<option value="null">Select</option>
		<g:each in="${enume?.enumOptions}" var="option">
			<option value="${option.value}"  ${option?.value==value?.enumValue ? 'selected':''}>
				<g:i18n field="${option.names}" />
			</option>
		</g:each>
	</select>
	</g:if>
	<g:else>
		<g:each in="${enume?.enumOptions}" var="option">
			<span class="display-in-block"><input type="checkbox" value="1" name="option.names" ${option?.value==value?.enumValue? 'checked="checked" ':''}/>
			<span><g:i18n field="${option.names}" /></span></span>
		</g:each>
	</g:else>
	<g:if test="${lastValue!=null}">
		<g:set var="option" value="${enume?.getOptionForValue(lastValue.enumValue)}"/>
		<span class="survey-old-value">(${option!=null?i18n(field: option.names):lastValue.enumValue})</span>
	</g:if>
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>