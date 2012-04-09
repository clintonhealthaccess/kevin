<div class="table-wrap">
	<table class="listing">
		<thead>
			<tr>
				<th/>
				<th>Name</th>
				<th>Data Locations</th>
				<th>Periods</th>
				<th>Data Location Types</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${entities}" status="i" var="export">
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
					<td>
	            		<ul class="horizontal">
			           		<li>
			           			<a class="edit-link" href="${createLinkWithTargetURI(controller:exporter, action:'edit', params:[id: export.id])}"><g:message code="default.link.edit.label" /></a>
							</li>
			           		<li>
			           			<a class="delete-link" href="${createLinkWithTargetURI(controller:'exporter', action:'delete', params:[id:export.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
							</li>
		           		</ul>
					</td>
					<td>${export.names}</td>
	  				<td>${export.email}</td>
	  				<td>${export.permissionString}</td>
	  				<td>${export.roles}</td>
	  				<td>
  						<a href="${createLinkWithTargetURI(controller:'exporter', action:'export', params:[id:user.id])}">download</a>
	  				</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>