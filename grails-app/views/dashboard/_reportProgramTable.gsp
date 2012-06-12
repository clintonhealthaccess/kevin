<table class="horizontal-graph">
<thead>
  <tr>
	<th><g:i18n field="${currentProgram.names}"/></th>
	<th><g:message code="dashboard.report.table.score"/></th>
	<th></th>
  </tr>
</thead>
<g:if test="${dashboard != null && dashboard.dashboardEntities != null && !dashboard.dashboardEntities.empty}">
	<tbody>
		<g:each in="${dashboard.dashboardEntities}" var="entity">			
			<tr>
				<td>
					<g:if test="${!entity.isTarget()}">
						<a href="${createLink(controller:'dashboard', action:'view',
							params:[period: currentPeriod.id, program: entity.program.id, location: currentLocation.id, dashboardEntity: entity.id])}">
							<g:i18n field="${entity.program.names}" />
						</a>
					</g:if>
					<g:else>
						<g:i18n field="${entity.names}" />
					</g:else>
				</td>
				<g:render template="percentageValue" model="[percentageValue: dashboard.getPercentage(currentLocation, entity), id: entity.id]"/>
			</tr>
		</g:each>
	</tbody>
</g:if>
</table>