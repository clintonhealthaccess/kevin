<g:if test="${!reportTargets.empty}">
	<table class="listing">
		<thead>
			<tr>
				<th><g:message code="reports.target.class.label"/></th>
				<th><g:message code="reports.target.label"/></th>
				<th><g:message code="reports.program.label"/></th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${reportTargets}" status="i" var="reportTarget"> 
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
					<td>${reportTarget.class.simpleName}</td>
					<td>${reportTarget.code}</td>
					<td>${reportTarget.program.code}</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</g:if>
<g:else>
	<div class="explanation-empty">
		No referencing report targets.
	</div>
</g:else>