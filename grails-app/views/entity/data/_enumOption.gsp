<div class="float-right">
	<span> 
		<a href="${createLinkWithTargetURI(controller:'enumOption', action:'edit', params:[id: option.id])}">
			<g:message code="general.text.edit" default="Edit" />
		</a>&nbsp; 
		<a href="${createLinkWithTargetURI(controller:'enumOption', action:'delete', params:[id: option.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
			<g:message code="general.text.delete" default="Delete" />
		</a>
	</span>
</div>
<div>
	<span class="bold">Option Name:</span> 
	<span>${i18n(field: option.names)}</span>
</div>
<div>
	<span class="bold">Code:</span> 
	<span>${option.code}</span>
</div>
<div>
	<span class="bold">Value:</span> 
	<span>${option.value}</span>
</div>
