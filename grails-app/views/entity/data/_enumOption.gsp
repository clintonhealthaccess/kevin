<div class="float-right">
	<span> 
		<a class="edit-link" href="${createLinkWithTargetURI(controller:'enumOption', action:'edit', params:[id: option.id])}">
			<g:message code="default.link.edit.label" default="Edit" />
		</a>&nbsp; 
		<a class="delete-link" href="${createLinkWithTargetURI(controller:'enumOption', action:'delete', params:[id: option.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
			<g:message code="default.link.delete.label" default="Delete" />
		</a>
	</span>
</div>
<div>
	<span class="bold">Option Name:</span> 
	<span>${i18n(field: option.names)}</span>
</div>
<div>
	<span class="bold">Value:</span> 
	<span>${option.value}</span>
</div>
