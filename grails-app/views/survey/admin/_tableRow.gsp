<div> 
	<a class="edit-link" href="${createLinkWithTargetURI(controller:'tableRow', action:'edit', id:row.id)}">
		<g:message code="default.link.edit.label" />
	</a>
	<a class="delete-link" href="${createLinkWithTargetURI(controller:'tableRow', action:'delete', id:row.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
		<g:message code="default.link.delete.label" />
	</a>
</div>
<div>
	<span class="bold"><g:message code="entity.locationtype.label"/>:</span>
	<span> ${row.typeCodeString}</span>
</div>
<div>
	<span class="bold"><g:message code="entity.order.label"/>:</span>
	<span>${row.order}</span>
</div>
<input type="hidden" name="rowNames" value="${index}"/>
<g:i18nRichTextarea name="rowNames[${index}].names" bean="${row}" value="${row.names}" height="50"
	label="${message(code:'survey.tablequestion.tablerow.name.label')}" field="names" />
