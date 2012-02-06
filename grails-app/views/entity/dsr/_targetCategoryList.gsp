<table class="listing">
	<thead>
		<tr>
			<th/>
			<th>Name</th>
			<th>Code</th>
			<th>Order</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="category">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'dashboardTarget', action:'edit', params:[id: category.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'dashboardTarget', action:'delete', params:[id: category.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
						</li>
						
					</ul>
				</td>
				<td>
					<g:i18n field="${category.names}"/>
				</td>
				<td>${category.code}</td>
				<td>${category.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>