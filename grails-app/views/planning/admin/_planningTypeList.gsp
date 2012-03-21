<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label" default="Name"/></th>
			<th><g:message code="rawDataElement.label" default="Period"/></th>
			<th><g:message code="default.number.label" args="[message(code:'planning.planningCost.label', default:"Planning costs")]"/></th>
			<th><g:message code="entity.list.manage.label" default="Manage"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="planningType">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'planningType', action:'edit', params:[id: planningType.id])}">
								<g:message code="default.link.edit.label" default="Edit" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'planningType', action:'delete', params:[id: planningType.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="default.link.delete.label" default="Delete" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${planningType.names}"/></td>
				<td><g:i18n field="${planningType.formElement.dataElement.names}"/></td>
				<td>${planningType.costs.size()}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected manage-btn" href="#"><g:message code="entity.list.manage.label" default="Manage"/></a>
						<div class="hidden manage-list dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'formValidationRule', action:'list', params:['formElement.id': planningType.formElement?.id])}"><g:message code="default.list.label" args="[message(code:'survey.validationrule.label',default:'Validation Rules')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'planningCost', action:'list', params:['planningType.id':planningType.id])}"><g:message code="default.list.label" args="[message(code:'planning.planningCost.label',default:'Planning Cost')]" /></a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>