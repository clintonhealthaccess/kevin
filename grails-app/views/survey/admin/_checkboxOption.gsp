<div> 
	<a class="edit-link" href="${createLinkWithTargetURI(controller:'checkboxOption', action:'edit', id:option.id)}">
		<g:message code="default.link.edit.label" />
	</a>
	<a class="delete-link" href="${createLinkWithTargetURI(controller:'checkboxOption', action:'delete', id:option.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
		<g:message code="default.link.delete.label" />
	</a>
	<g:if test="${option.surveyElement != null}">
        <a href="${createLink(controller:'surveyValidationRule', action:'list', params:['formElement.id': option.surveyElement.id])}">
        <g:message code="survey.viewvalidationrule.label" />
        </a> 
	</g:if>
</div>
<div>
	<span class="bold"><g:message code="dataelement.label"/>:</span>
	<span>${i18n(field:option.surveyElement?.dataElement?.names)}</span>
</div>
<div>
	<span class="bold"><g:message code="facility.type.label"/>:</span>
	<span>${option.typeCodeString}</span>
</div>
<div>
	<span class="bold"><g:message code="survey.checkboxquestion.checkboxoption.order.label"/>:</span>
	<span>${option.order}</span>
</div>
<input type="hidden" name="optionNames" value="${index}"/>
<g:i18nRichTextarea name="optionNames[${index}].names" bean="${option}" value="${option.names}" height="50"
	label="${message(code:'survey.checkboxquestion.checkboxoption.name.label')}" field="names" />
