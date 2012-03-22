<div> 
	<a class="edit-link" href="${createLinkWithTargetURI(controller:'tableColumn', action:'edit', id:column.id)}">
		<g:message code="default.link.edit.label" />
	</a>
	<a class="delete-link" href="${createLinkWithTargetURI(controller:'tableColumn', action:'delete', id:column.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
		<g:message code="default.link.delete.label" />
	</a>
</div>
<div>
	<span class="bold"><g:message code="entity.locationtype.label"/>:</span>
	<span>${column.typeCodeString}</span>
</div>
<div>
	<span class="bold"><g:message code="entity.order.label"/>:</span>
	<span>${column.order}</span>
</div>
<input type="hidden" name="columnNames" value="${index}"/>
<g:i18nRichTextarea name="columnNames[${index}].names" bean="${column}" value="${column.names}" height="50"
	label="${message(code:'survey.tablequestion.tablecolumn.name.label')}" field="names" />
