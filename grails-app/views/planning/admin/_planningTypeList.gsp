<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label"/></th>
			<th><g:message code="rawdataelement.label"/></th>
			<th><g:message code="default.number.label" args="[message(code:'planning.planningCost.label')]"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="planningType">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'planningType', action:'edit', params:[id: planningType.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'planningType', action:'delete', params:[id: planningType.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${planningType.names}"/></td>
				<td><g:i18n field="${planningType.formElement.dataElement.names}"/></td>
				<td>${planningType.costs.size()}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected manage-btn" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="hidden manage-list dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'formValidationRule', action:'list', params:['formElement.id': planningType.formElement?.id])}"><g:message code="default.list.label" args="[message(code:'survey.validationrule.label')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'planningCost', action:'list', params:['planningType.id':planningType.id])}"><g:message code="default.list.label" args="[message(code:'planning.planningCost.label')]" /></a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>