<table>
	<thead>
		<tr>
			<th>Name</th>
			<th>Inactive</th>
			<th>Value</th>
			<th>Order</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="option"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><g:i18n field="${option.names}" /></td>
				<td>${option.inactive?'\u2713':''}</td>
				<td>${option.value}</td>
				<td>${option.order}</td>
				<td>
					<div class="dropdown white-dropdown"> 
					     <a class="selected" href="#" data-type="section">Manage</a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
						        	<a href="${createLinkWithTargetURI(controller:'enumOption', action:'edit', params:[id: option.id])}">
										<g:message code="general.text.edit" default="Edit" />
									</a>
								</li>
								<li class="hidden">
									<a href="${createLinkWithTargetURI(controller:'enumOption', action:'delete', params:[id: option.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
										<g:message code="general.text.delete" default="Delete" />
									</a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
