<table class='horizontal-graph'>
<thead>
  <tr>
	<th><g:i18n field="${currentLocation.names}"/></th>
	<th>Score</th>
	<th></th>
  </tr>
</thead>
<g:if test="dashboard.organisations != null && !dashboard.organisations.empty}">
	<tbody>
		<g:each in="${dashboard.locations}" var="location">
			<g:if test="${location != currentLocation}">
				<tr>
					<g:set var="percentageValue" />
					<td><g:if test="${!location.collectsData()}">
							<a href="${createLink(controller:'dashboard', action:'view', 
						params:[period: currentPeriod.id, objective: currentObjective.id, location: location.id])}">
								<g:i18n field="${location.names}" />
							</a>
						</g:if> <g:else>
							<g:i18n field="${location.names}" />
						</g:else>
					</td>
					<td><g:set var="percentageValue" value="${dashboard.getPercentage(location, dashboardEntity)}" />
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
								data-entity="${currentObjective.id}"
								data-percentage="${percentageValue}"
								style="width:0%;"
								original-title="${percentageValue}%"></div>
						</g:if> 
						<g:elseif test="${percentageValue > 100}">
							<div class="js_bar_horizontal tooltip horizontal-bar expand-bar"
								data-entity="${currentObjective.id}"
								data-percentage="${percentageValue}"
								style="width:100%;"
								original-title="${percentageValue}%"></div>
						</g:elseif> 
						<g:else>
							<div class="js_bar_horizontal tooltip horizontal-bar"
								data-entity="${currentObjective.id}"
								data-percentage="${percentageValue}"
								style="width:${percentageValue}%;"
								original-title="${percentageValue}%"></div>
						</g:else>
					</td>
				</tr>
			</g:if>
		</g:each>
	</tbody>
</g:if>
</table>
<!-- comparison value -->
<div class="horizontal-graph-average" data-entity="${dashboardEntity.id}">
	<div class="horizontal-graph-tip tooltip" style="left: 63%;" title="63%" data-percentage="63">?</div>
	<div class="horizontal-graph-marker"></div>
</div>