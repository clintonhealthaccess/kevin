<!-- Value type question -->
<g:set var="surveyElement" value="${surveyElementValue.surveyElement}"/>
<g:set var="dataValue" value="${surveyElementValue.dataValue}"/>

<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
<input type="text" value="${dataValue?.value}" name="surveyElements[${surveyElement.id}].dataValue.value" class="idle-field" />
<div class="error-list"><g:renderUserErrors element="${surveyElementValue}"/></div>