<% def dataService = application.getAttribute("org.codehaus.groovy.grails.APPLICATION_CONTEXT").getBean("dataService") %>
<g:set var="enume" value="${dataService.getEnum(type.enumId)}"/>

<!-- Enum type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-enum ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<select name="surveyElements[${surveyElement.id}].value${suffix}" ${readonly?'disabled="disabled"':''}>
		<option value="null">Select</option>
		<g:each in="${enume?.enumOptions}" var="option">
			<option value="${option.value}"  ${option?.value==value?.enumValue ? 'selected':''}>
				<g:i18n field="${option.names}" />
			</option>
		</g:each>
	</select>
	<g:if test="${lastValue!=null}">
		<g:set var="option" value="${enume.getOptionForValue(lastValue)}"/>
		<span class="survey-old-value">(${option!=null?i18n(field: option.names):lastValue})</span>
	</g:if>
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>