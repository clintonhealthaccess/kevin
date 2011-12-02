<table class="listing">
	<thead>
		<tr>
			<th></th>
			<th>Id</th>
			<th>Type</th>
			<th>Code</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${referencingData}" var="data" status="i">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<a class="edit-link" href="${createLinkWithTargetURI(controller:data.class.simpleName, action:'edit', params:[id: data.id])}">
						<g:message code="default.link.edit.label" default="Edit" />
					</a>
				</td>
				<td>${data.id}</td>
				<td>${data.class.simpleName}</td>
				<td>${data.code}</td>
			</tr>
		</g:each>
	</tbody>
</table>