<div class="float-right">
	<span> 
		<g:link controller="checkboxOption" action="edit" id="${option.id}" class="flow-edit-option"><g:message code="general.text.edit" default="Edit" /></g:link>  
		<g:link controller="checkboxOption" action="delete" id="${option.id}" class="flow-delete"><g:message code="general.text.delete" default="Delete" /></g:link>  
		<g:if test="${option.surveyElement != null}">
	        <a href="${createLink(controller:'surveyValidationRule', action:'list', params:[elementId: option.surveyElement.id])}">
	          View Validation Rules
	        </a> 
        </g:if>
	</span>
</div>
<div>
	<span class="bold">Option Name:</span><span> ${i18n(field: option.names)}</span>
</div>
<div>
	<span class="bold">Data Element:</span><span>${i18n(field:option.surveyElement?.dataElement?.names)}</span>
</div>
<div>
	<span class="bold">Organisation Unit Group:</span><span> ${option.groupUuidString}</span>
</div>