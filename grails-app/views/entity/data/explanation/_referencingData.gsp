<g:if test="${!referencingData.isEmpty()}">
	<table class="listing">
		<thead>
			<tr>
				<th><g:message code="entity.id.label"/></th>
				<th><g:message code="entity.type.label"/></th>
				<th><g:message code="entity.code.label"/></th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${referencingData}" var="data" status="i">
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
					<td>${data.id}</td>
					<td>${data.class.simpleName}</td>
					<td>${data.code}</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</g:if>
<g:else>
	<div class="explanation-empty">
		No referencing data.
	</div>
</g:else>
