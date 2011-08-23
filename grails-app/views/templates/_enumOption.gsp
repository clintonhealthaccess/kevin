<div class="float-right hidden">
	<span> <g:link controller="enumOption" action="edit"
			id="${option.id}" class="flow-edit-option">
			<g:message code="general.text.edit" default="Edit" />
		</g:link>&nbsp; <g:link controller="enumOption" action="delete"
			id="${option.id}" class="flow-delete">
			<g:message code="general.text.delete" default="Delete" />
		</g:link> </span>
</div>
<div class="option-element">
	<label>Option Name:</label> <span> ${i18n(field: option.names)}</span>
</div>
<div class="option-element">
	<label>Code:</label> <span>
		${option.code}</span>
</div>
<div class="option-element">
	<label>Value:</label> <span> ${option.value}</span>
</div>
