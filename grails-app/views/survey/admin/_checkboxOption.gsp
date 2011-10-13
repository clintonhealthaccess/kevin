<div class="float-right">
	<span> 
		<a href="${createLinkWithTargetURI(controller:'checkboxOption', action:'edit', id:option.id)}">
			<g:message code="general.text.edit" default="Edit" />
		</a>
		<a href="${createLinkWithTargetURI(controller:'checkboxOption', action:'delete', id:option.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
			<g:message code="general.text.delete" default="Delete" />
		</a>
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