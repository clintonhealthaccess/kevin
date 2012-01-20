<table class='horizontal-graph'>
<thead>
  <tr>
	<th><g:i18n field="${currentOrganisation.names}"/></th>
	<th>Score</th>
	<th></th>
  </tr>
</thead>
<g:if test="dashboard.organisations != null && !dashboard.organisations.isEmpty()}">
	<tbody>
		<g:each in="${dashboard.organisations}" var="organisation">
			<g:if test="${organisation != currentOrganisation}">
				<tr>
					<g:set var="percentageValue" />
					<td><g:if test="${!organisation.collectsData()}">
							<a href="${createLink(controller:'dashboard', action:'view', 
						params:[period: currentPeriod.id, objective: currentObjective.id, organisation: organisation.id])}">
								<g:i18n field="${organisation.names}" />
							</a>
						</g:if> <g:else>
							<g:i18n field="${organisation.names}" />
						</g:else>
					</td>
					<td><g:set var="percentageValue" value="${dashboard.getPercentage(organisation, dashboardEntity)}" />
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