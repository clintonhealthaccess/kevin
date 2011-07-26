<div class="float-right">
	<span> <g:link controller="checkboxOption" action="edit"
			id="${option.id}" class="flow-edit-option">
			<g:message code="general.text.edit" default="Edit" />
		</g:link>&nbsp; <g:link controller="checkboxOption" action="delete"
			id="${option.id}" class="flow-delete">
			<g:message code="general.text.delete" default="Delete" />
		</g:link> </span>
</div>
<div class="option-element">
	Option Name: <span> ${i18n(field: option.names)}</span>
</div>
<div class="option-element">
	Data Element: <span>
		${i18n(field:option.surveyElement?.dataElement?.names)}</span>
</div>
<div class="option-element">
	Organisation Unit Group: <span> ${option.groupUuidString}</span>
</div>
