<table class="listing">
	<thead>
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Value</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="constant">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><g:link action="edit" id="${constant.id}"><g:i18n field="${constant.names}"/></g:link></td>
				<td><g:i18n field="${constant.descriptions}"/></td>
				<td>${constant.value}</td>
				<td><a href="${createLink(controller:'constant', action:'delete', id:constant.id)}">delete</a></td>
			</tr>
		</g:each>
	</tbody>
</table>