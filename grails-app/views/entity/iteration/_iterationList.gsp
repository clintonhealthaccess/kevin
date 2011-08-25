<table>
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
				<td><g:link class="flow-edit" action="edit" id="${iteration.id}">${iteration.startDate}</g:link></td>
				<td><g:link class="flow-edit" action="edit" id="${iteration.id}">${iteration.endDate}</g:link></td>
				<td><g:link class="flow-delete" action="delete" id="${iteration.id}"><g:message code="general.text.delete" default="Delete" /></g:link></td>
			</tr>
		</g:each>
	</tbody>
</table>
