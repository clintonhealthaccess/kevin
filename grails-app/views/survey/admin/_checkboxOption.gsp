<div> 
	<a class="edit-link" href="${createLinkWithTargetURI(controller:'checkboxOption', action:'edit', id:option.id)}">
		<g:message code="default.link.edit.label" default="Edit" />
	</a>
	<a class="delete-link" href="${createLinkWithTargetURI(controller:'checkboxOption', action:'delete', id:option.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
		<g:message code="default.link.delete.label" default="Delete" />
	</a>
	<g:if test="${option.surveyElement != null}">
        <a href="${createLink(controller:'surveyValidationRule', action:'list', params:['surveyElement.id': option.surveyElement.id])}">
        <g:message code="survey.viewvalidationrule.label" default="View Validation Rules" />
        </a> 
	</g:if>
</div>
<div>
	<span class="bold"><g:message code="dataelement.label" default="Data Element"/>:</span>
	<span>${i18n(field:option.surveyElement?.dataElement?.names)}</span>
</div>
<div>
	<span class="bold"><g:message code="facility.type.label" default="Facility Groups"/>:</span>
	<span>${option.typeCodeString}</span>
</div>
<div>
	<span class="bold"><g:message code="survey.checkboxquestion.checkboxoption.order.label" default="Order"/>:</span>
	<span>${option.order}</span>
</div>
<input type="hidden" name="optionNames" value="${index}"/>
<g:i18nRichTextarea name="optionNames[${index}].names" bean="${option}" value="${option.names}" height="50"
	label="${message(code:'survey.checkboxquestion.checkboxoption.name.label', default:'Name')}" field="names" />
