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
				<g:set var="percentageValue" />
				<td><g:if test="${!location.collectsData()}">
						<a href="${createLink(controller:'dashboard', action:'view', 
						params:[period: currentPeriod.id, program: currentProgram.id, location: location.id])}">
							<g:i18n field="${location.names}" />
						</a>
					</g:if> <g:else>
						<g:i18n field="${location.names}" />
					</g:else>
				</td>
				<td><g:set var="percentageValue" value="${dashboard.getPercentage(location, dashboardEntity)}" />
					<g:if test="${percentageValue != null}">
						${percentageValue}%
					</g:if>
					<g:else>
						<g:message code="report.value.na"/>
					</g:else></td>
				<td>
					<!-- percentage value --> 
					<g:if test="${percentageValue == null}">
						<div class="js_bar_horizontal tooltip horizontal-bar"
							data-entity="${currentProgram.id}"
							data-percentage="${percentageValue}"
							style="width:0%;"
							original-title="${percentageValue}"></div>
					</g:if>
					<g:elseif test="${percentageValue <= 100}">
						<div class="js_bar_horizontal tooltip horizontal-bar"
							data-entity="${currentProgram.id}"
							data-percentage="${percentageValue}"
							style="width:${percentageValue}%;"
							original-title="${percentageValue}%"></div>
					</g:elseif>
					<g:else>
						<div class="js_bar_horizontal tooltip horizontal-bar expand-bar"
							data-entity="${currentProgram.id}"
							data-percentage="${percentageValue}"
							style="width:100%;"
							original-title="${percentageValue}%"></div>
					</g:else> 					
				</td>
			</tr>
		</g:each>
	</tbody>
</g:if>
</table>
<!-- comparison value -->
<div class="horizontal-graph-avg hidden" data-entity="${dashboardEntity.id}">
	<div class="horizontal-graph-tip tooltip" style="left: 63%;" title="63%" data-percentage="63">?</div>
	<div class="horizontal-graph-marker"></div>
</div>