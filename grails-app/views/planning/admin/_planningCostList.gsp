<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label"/></th>
			<th>Type</th>
			<th>Discriminator value</th>
			<th>Grouping section</th>
			<th>Sum</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="planningCost">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'planningCost', action:'edit', params:[id: planningCost.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'planningCost', action:'delete', params:[id: planningCost.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${planningCost.names}"/></td>
				<td>${planningCost.type}</td>
				<td>${planningCost.discriminatorValueString}</td>
				<td>${planningCost.groupSection}</td>
				<td><g:i18n field="${planningCost.dataElement.names}"/></td>
			</tr>
		</g:each>
	</tbody>
</table>