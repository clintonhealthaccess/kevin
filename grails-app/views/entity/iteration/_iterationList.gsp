<table class="listing">
	<thead>
		<tr>
			<th><g:message code="general.text.startdate" default="Start Date" /></th>
			<th><g:message code="general.text.enddate" default="End Date" /></th>
			<th>Action </th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="iteration">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<a href="${createLinkWithTargetURI(controller:'iteration', action:'edit', params:[id: iteration.id])}">
						${iteration.startDate}
					</a>
				</td>
				<td>
					<a href="${createLinkWithTargetURI(controller:'iteration', action:'edit', params:[id: iteration.id])}">
						${iteration.endDate}
					</a>
				</td>
				<td>
					<a href="${createLinkWithTargetURI(controller:'iteration', action:'delete', params:[id: iteration.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
						<g:message code="general.text.delete" default="Delete" />
					</a>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
