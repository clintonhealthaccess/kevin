<table>
	<thead>
		<tr>
			<th>Names</th>
			<th>Descriptions</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="expression">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<g:link action="edit" id="${expression.id}"  class="flow-edit">
						<g:i18n field="${expression.names}"/>
					</g:link>
				</td>
				<td><g:i18n field="${expression.descriptions}"/></td>
				<td><a class="flow-delete" href="${createLink(controller:'expression', action:'delete', id:expression.id)}">delete</a></td>
			</tr>
		</g:each>
	</tbody>
</table>
