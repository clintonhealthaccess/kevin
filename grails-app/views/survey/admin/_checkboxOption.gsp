<div class="float-right">
	<span> 
		<g:link controller="checkboxOption" action="edit" id="${option.id}" class="flow-edit-option"><g:message code="general.text.edit" default="Edit" /></g:link>  
		<g:link controller="checkboxOption" action="delete" id="${option.id}" class="flow-delete"><g:message code="general.text.delete" default="Delete" /></g:link>  
		<g:if test="${option.surveyElement != null}">
	        <a href="${createLink(controller:'surveyValidationRule', action:'list', params:[elementId: option.surveyElement.id])}">
	        <g:message code="survey.viewvalidationrule.label" default="View Validation Rules" />
	        </a> 
        </g:if>
	</span>
</div>
<div>
	<span class="bold"><g:message code="survey.optionname.label" default="Option Name"/>:</span><span> ${i18n(field: option.names)}</span>
</div>
<div>
	<span class="bold"><g:message code="survey.dataelement.label" default="Data Element"/>:</span><span>${i18n(field:option.surveyElement?.dataElement?.names)}</span>
</div>
<div>
	<span class="bold"><g:message code="general.text.facilitygroups" default="Facility Groups"/>:</span><span> ${option.groupUuidString}</span>
</div>