<table>
	<thead>
		<tr>
			<th>Names</th>
			<th>Type</th>
			<th>Code</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="expression">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<a href="${createLink(action:'edit', id: expression.id)}" title="${i18n(field: expression.names)}"  
					rel="${createLink(controller:'expression', action:'getDescription', id:expression.id)}" 
					class="flow-edit cluetip">
						<g:i18n field="${expression.names}"/>
					</a>
				</td>
				<td><g:toHtml value="${expression.type.getDisplayedValue(2)}"/></td>
				<td>${expression.code}</td>
				<td><a class="flow-delete" href="${createLink(controller:'expression', action:'delete', id:expression.id)}">delete</a></td>
			</tr>
		</g:each>
	</tbody>
</table>
