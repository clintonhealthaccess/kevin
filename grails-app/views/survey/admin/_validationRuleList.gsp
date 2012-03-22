<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="dataelement.label"/></th>
			<th><g:message code="survey.validationrule.prefix.label"/></th>
			<th><g:message code="expression.label"/></th>
			<th><g:message code="survey.validationrule.allowoutlier.label"/></th>
			<th><g:message code="facility.type.label"/></th>
			<th><g:message code="survey.validationrule.message.label"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="validationRule">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'surveyValidationRule', action:'edit', params:[id: validationRule.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'surveyValidationRule', action:'delete', params:[id: validationRule.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${validationRule.surveyElement.dataElement?.names}" /></td>
				<td>${validationRule.prefix}</td>
				<td>${validationRule.expression}</td>
				<td> 
				    <g:if test="${validationRule.allowOutlier==true}">
				    &radic;
				    </g:if>
			        <g:else>
			        X
			        </g:else>
			    </td>
			    <td>${validationRule.typeCodeString}</td>
				<td><g:i18n field="${validationRule.messages}" /></td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected manage-btn" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="hidden manage-list dropdown-list js_dropdown-list">
							<ul>
								<li>
							 		<a href="${createLinkWithTargetURI(controller:'surveyValidationRule', action:'copy', params:[id: validationRule.id])}"><g:message code="survey.clone.label" /> </a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
