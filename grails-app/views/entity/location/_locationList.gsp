<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="location.name.label" default="Name"/></th>
			<th>Code</th>
			<th>Level</th>
			<th>Parent</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="location">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'location', action:'edit', params:[id: location.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'location', action:'delete', params:[id: location.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
						</li>
						
					</ul>
				</td>
				<td>${location.code}</td>
				<td>
					<g:i18n field="${location.names}"/>
				</td>
				<td>
					<g:i18n field="${location.level.names}"/>
				</td>
				<td>
					<g:i18n field="${location.parent?.names}"/>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>