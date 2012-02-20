<table class="listing">
	<thead>
		<tr>
			<th/>
			<th>Objective</th>
			<th>Weight</th>
			<th>Order</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="objective">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'dashboardObjective', action:'edit', params:[id: objective.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'dashboardObjective', action:'delete', params:[id: objective.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
						</li>
						
					</ul>
				</td>
				<td>
					<g:i18n field="${objective.reportObjective.names}"/>
				</td>
				<td>${objective.weight}</td>
				<td>${objective.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>