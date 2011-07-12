<!-- Enum type question -->
<g:set var="surveyElement" value="${surveyElementValue.surveyElement}"/>
<g:set var="dataValue" value="${surveyElementValue.dataValue}"/>

<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
<select name="surveyElements[${surveyElement.id}].dataValue.value">
	<option value="">Select</option>
	<g:each in="${surveyElement.dataElement.enume?.enumOptions}" var="option">
		<option value="${option.value}"  ${option?.value==dataValue?.value ? 'selected':''}>
			<g:i18n field="${option?.names}" />
		</option>
	</g:each>
</select>
<div class="error-list"><g:renderUserErrors element="${surveyElementValue}"/></div>