<table class="listing">
	<thead>
		<tr>
			<th/>
			<th>Program</th>
			<th>Weight</th>
			<th>Order</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="program">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'dashboardProgram', action:'edit', params:[id: program.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'dashboardProgram', action:'delete', params:[id: program.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
						</li>
						
					</ul>
				</td>
				<td>
					<g:i18n field="${program.reportProgram.names}"/>
				</td>
				<td>${program.weight}</td>
				<td>${program.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>