<table>
	<thead>
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Code</th>
			<th>Value</th>
			<th>Order</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="option"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><g:i18n field="${option.names}" /></td>
				<td><g:i18n field="${option.descriptions}" /></td>
				<td>${option.code}</td>
				<td>${option.value}</td>
				<td>${option.order}</td>
				<td>
					<div class="dropdown white-dropdown"> 
					     <a class="selected" href="#" data-type="section">Manage</a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
						        	<g:link controller="enumOption" action="edit" id="${option.id}" class="flow-edit">
										<g:message code="general.text.edit" default="Edit" />
									</g:link>
								</li>
								<li class="hidden">
									<g:link controller="enumOption" action="delete" id="${option.id}" class="flow-delete">
										<g:message code="general.text.delete" default="Delete" />
									</g:link>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
