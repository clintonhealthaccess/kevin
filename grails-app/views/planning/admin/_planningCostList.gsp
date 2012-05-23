<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label"/></th>
			<th><g:message code="planning.planningcost.type.label"/></th>
			<th><g:message code="planning.planningcost.dataelement.label"/></th>
			<th><g:message code="planning.planningcost.hideifzero.label"/></th>
			<th><g:message code="entity.order.label"/></th>
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
				<td><g:i18n field="${planningCost.dataElement.names}"/>[${planningCost.dataElement.id}]</td>
				<td>${planningCost?.hideIfZero?'\u2713':''}</td>
				<td>${planningCost.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>