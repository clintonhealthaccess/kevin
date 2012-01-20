<div> 
	<a class="edit-link" href="${createLinkWithTargetURI(controller:'tableRow', action:'edit', id:row.id)}">
		<g:message code="default.link.edit.label" default="Edit" />
	</a>
	<a class="delete-link" href="${createLinkWithTargetURI(controller:'tableRow', action:'delete', id:row.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
		<g:message code="default.link.delete.label" default="Delete" />
	</a>
</div>
<div>
	<span class="bold"><g:message code="facility.type.label" default="Facility Groups"/>:</span>
	<span> ${row.typeCodeString}</span>
</div>
<div>
	<span class="bold"><g:message code="survey.tablequestion.tablerow.order.label" default="Order"/>:</span>
	<span>${row.order}</span>
</div>
<input type="hidden" name="rowNames" value="${index}"/>
<g:i18nRichTextarea name="rowNames[${index}].names" bean="${row}" value="${row.names}" height="50"
	label="${message(code:'survey.tablequestion.tablerow.name.label', default:'Name')}" field="names" />
