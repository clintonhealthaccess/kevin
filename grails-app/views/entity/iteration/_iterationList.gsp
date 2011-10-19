<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="general.text.startdate" default="Start Date" /></th>
			<th><g:message code="general.text.enddate" default="End Date" /></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="iteration">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'iteration', action:'edit', params:[id: iteration.id])}">
								<g:message code="general.text.edit" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'iteration', action:'delete', params:[id: iteration.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="general.text.delete" default="Delete" /></a>
						</li>
						
					</ul>
				</td>
				<td>${iteration.startDate}</td>
				<td>${iteration.endDate}</td>
			</tr>
		</g:each>
	</tbody>
</table>
