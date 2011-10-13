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
					<a href="${createLinkWithTargetURI(action:'edit', id: expression.id)}" title="${i18n(field: expression.names)}"  
					rel="${createLink(controller:'expression', action:'getDescription', id:expression.id)}" 
					class="cluetip">
						<g:i18n field="${expression.names}"/>
					</a>
				</td>
				<td><g:toHtml value="${expression.type.getDisplayedValue(2)}"/></td>
				<td>${expression.code}</td>
				<td>
					<a href="${createLinkWithTargetURI(controller:'expression', action:'delete', params:[id:expression.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
						Delete
					</a>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
