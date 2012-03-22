<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="period.startdate.label" /></th>
			<th><g:message code="period.enddate.label" /></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="period">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'period', action:'edit', params:[id: period.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'period', action:'delete', params:[id: period.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
						</li>
						
					</ul>
				</td>
				<td>${period.startDate}</td>
				<td>${period.endDate}</td>
			</tr>
		</g:each>
	</tbody>
</table>
