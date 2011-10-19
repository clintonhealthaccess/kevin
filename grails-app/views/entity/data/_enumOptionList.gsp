<table class="listing">
	<thead>
		<tr>
			<th/>
			<th>Name</th>
			<th>Inactive</th>
			<th>Value</th>
			<th>Order</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="option"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
				        	<a class="edit-link" href="${createLinkWithTargetURI(controller:'enumOption', action:'edit', params:[id: option.id])}">
								<g:message code="general.text.edit" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'enumOption', action:'delete', params:[id: option.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="general.text.delete" default="Delete" />
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${option.names}" /></td>
				<td>${option.inactive?'\u2713':''}</td>
				<td>${option.value}</td>
				<td>${option.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>
