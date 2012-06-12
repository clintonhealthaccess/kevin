<table class="horizontal-graph">
<thead>
  <tr>
	<th><g:i18n field="${currentLocation.names}"/></th>
	<th><g:message code="dashboard.report.table.score"/></th>
	<th></th>
  </tr>
</thead>
<g:if test="${dashboard != null && dashboard.locations != null && !dashboard.locations.empty}">
	<tbody>
		<g:each in="${dashboard.locations}" var="location">
			<tr>
				<td>
					<g:if test="${!location.collectsData()}">
						<a href="${createLink(controller:'dashboard', action:'view', 
							params:[period: currentPeriod.id, program: currentProgram.id, location: location.id])}">
							<g:i18n field="${location.names}" />
						</a>
					</g:if>
					<g:else>
						<g:i18n field="${location.names}" />
					</g:else>
				</td>
				<g:render template="percentageValue" model="[percentageValue: dashboard.getPercentage(location, dashboardEntity), id: location.id]"/>
			</tr>
		</g:each>
	</tbody>
</g:if>
</table>
