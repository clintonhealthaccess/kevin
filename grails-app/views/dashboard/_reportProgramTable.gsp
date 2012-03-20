<table class="horizontal-graph">
<thead>
  <tr>
	<th><g:i18n field="${currentProgram.names}"/></th>
	<th>Score</th>
	<th></th>
  </tr>
</thead>
<g:if test="${dashboard != null && dashboard.dashboardEntities != null && !dashboard.dashboardEntities.empty}">
	<tbody>
		<g:each in="${dashboard.dashboardEntities}" var="entity">			
			<tr>
				<g:set var="percentageValue" />
				<td><g:if test="${!entity.isTarget()}">
						<a href="${createLink(controller:'dashboard', action:'view',
						params:[period: currentPeriod.id, program: entity.program.id, location: currentLocation.id, dashboardEntity: entity.id])}">
							<g:i18n field="${entity.program.names}" />
						</a>
					</g:if> <g:else>
						<g:i18n field="${entity.names}" />
					</g:else></td>
				<td><g:set var="percentageValue" value="${dashboard.getPercentage(currentLocation, entity)}" />
					<g:if test="${percentageValue != null}">
							${percentageValue}%
					</g:if><g:else>
						<g:set var="percentageValue" value="N/A" />
						${percentageValue}
					</g:else></td>
				<td>
					<!-- percentage value -->
					<g:if test="${percentageValue == 'N/A'}">
						<div class="js_bar_horizontal tooltip horizontal-bar" 
							data-percentage="${percentageValue}"
							style="width:0%"							 
							original-title="${percentageValue}"></div>
					</g:if>
					<g:elseif test="${percentageValue <= 100}">
						<div class="js_bar_horizontal tooltip horizontal-bar" 
							data-percentage="${percentageValue}"
							style="width:${percentageValue}%"							 
							original-title="${percentageValue}%"></div>
					</g:elseif>
					<g:else>
						<div class="js_bar_horizontal tooltip horizontal-bar expand-bar" 
							data-percentage="${percentageValue}"
							style="width:100%"							 
							original-title="${percentageValue}%"></div>
					</g:else>						
					<!-- comparison value -->
					<div id="compare-dashboard-entity-${entity.id}" 
					class="js_bar_horizontal tooltip horizontal-bar-avg" 							
						data-percentage="45" 
						style="width:45%;" 
						original-title="45%"></div>
				</td>
			</tr>
		</g:each>
	</tbody>
</g:if>
</table>