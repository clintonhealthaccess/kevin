<g:if test="${data!=null}">
	<div class="push-10">
		<g:message code="dataelement.label"/>: <g:i18n field="${data.names}"/> - ${data.code}
	</div>
</g:if>
<div class="main">
	<g:render template="/entity/value/periodTabs"/>
	<g:each in="${periods}" var="period" status="periodIndex">
		<table id="listing-${period.id}" class="listing ${periodIndex!=0?'hidden':''}">
			<g:if test="${entities[period].empty}">
				<tr>
					<td colspan="4"><g:message code="entity.list.empty.label" args="[entityName]"/></td>
				</tr>
			</g:if>	
			<g:else>	
				<thead>
					<tr>
						<th><g:message code="dataelementvalue.location.label"/></th>
						<th><g:message code="dataelementvalue.value.label"/></th>
					</tr>
				</thead>
				<tbody>
					<g:each in="${entities[period]}" status="i" var="value"> 
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td><g:i18n field="${value.location.names}"/></td>
							<td><g:adminValue value="${value.value}" type="${value.data.type}"/></td>
						</tr>
					</g:each>
				</tbody>
			</g:else>
		</table>
	</g:each>
</div>