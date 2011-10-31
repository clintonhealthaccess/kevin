<div> 
	<a class="edit-link" href="${createLinkWithTargetURI(controller:'tableColumn', action:'edit', id:column.id)}">
		<g:message code="default.link.edit.label" default="Edit" />
	</a>
	<a class="delete-link" href="${createLinkWithTargetURI(controller:'tableColumn', action:'delete', id:column.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
		<g:message code="default.link.delete.label" default="Delete" />
	</a>
</div>
<div>
	<span class="bold"><g:message code="facility.type.label" default="Facility Groups"/>:</span>
	<span> ${column.groupUuidString}</span>
</div>
<input type="hidden" name="columnNames" value="${index}"/>
<g:i18nRichTextarea name="columnNames[${index}].names" bean="${column}" value="${column.names}" height="50"
	label="${message(code:'survey.tablequestion.tablecolumn.name.label', default:'Name')}" field="names" />
