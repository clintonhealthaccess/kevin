<!-- Text type question -->
<g:set var="surveyElement" value="${surveyElementValue.surveyElement}"/>
<g:set var="dataValue" value="${surveyElementValue.dataValue}"/>

<span class="display-in-block">

<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
<textarea name="surveyElements[${surveyElement.id}].dataValue.value" cols="100" rows="8" class="idle-field">${dataValue?.value}</textarea>
<div class="error-list"><g:renderUserErrors element="${surveyElementValue}"/></div>

</span>