<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="locationLevel.name.label" default="Name"/></th>
			<th>Code</th>
			<th>Order</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="locationLevel">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'locationLevel', action:'edit', params:[id: locationLevel.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'locationLevel', action:'delete', params:[id: locationLevel.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
						</li>
						
					</ul>
				</td>
				<td>
					<g:i18n field="${locationLevel.names}"/>
				</td>
				<td>${locationLevel.code}</td>
				<td>${locationLevel.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>