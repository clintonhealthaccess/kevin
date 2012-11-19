<!-- chart legend -->
<ul class="horizontal chart_legend">
	<g:if test="${fctTable != null && fctTable.indicators != null && !fctTable.indicators.empty}">
		<g:each in="${fctTable.indicators}" var="indicator" status="i">
			<li>
			<span class="${i == fctTable.indicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}"></span>
			<g:i18n field="${indicator.names}" /></li>
		</g:each>
	</g:if>
</ul>
<br />
<!-- chart y-axis -->
<ul class="chart">
	<li>100%</li>
	<li>50%</li>
	<li>0%</li>
</ul>
<!-- chart -->
<table class="vertical-graph">
	<tbody>
		<tr>
			<g:each in="${fctTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes)}" var="location">
				<td>
					<g:if test="${!fctTable.indicators.empty}">
						
						<!-- max indicator --><g:set var="maxIndicator" value="${''}"/>
						<!-- max report average between 0 and ~1 --><g:set var="maxAverage" value="${0}"/>
						<!-- total report average between 0 and ~1 --><g:set var="totalAverage" value="${0}"/>						
						
						<!-- stacked bars -->
						<div class="bars-vertical" data-location="${location.code}">
						
							<g:each in="${fctTable.indicators.reverse()}" var="indicator" status="i">
								<g:set var="value" value="${fctTable.getTableReportValue(location, indicator)}"/>									
								<g:if test="${value != null && !value.value.isNull() && !value.average.isNull()}">											
									
									<!-- report value number --><g:set var="reportBarValue" value="${g.reportBarData(value: fctTable.getTableReportValue(location, indicator)?.getValue(), type: indicator.type)}"/>											
									<!-- report value number 0-1 --><g:set var="reportBarAverage" value="${fctTable.getTableReportValue(location, indicator)?.getAverage().numberValue.round(2)}"/>
									<!-- report value percentage 0%-100% --><g:set var="reportBarPercentage" value="${g.reportBarData(value: fctTable.getTableReportValue(location, indicator)?.getAverage(), type: indicator.type, format: indicator.percentageFormat?:'#%', rounded: '2')}"/>

									<g:set var="maxIndicator" value="${reportBarAverage > maxAverage ? indicator.code : maxIndicator}"/>
									<g:set var="maxAverage" value="${reportBarAverage > maxAverage ? reportBarAverage : maxAverage}"/>
									<g:set var="totalAverage" value="${totalAverage + reportBarAverage}"/>
									
									<!-- stacked bar -->
									<div class="js_bar_vertical bar-vertical tooltip ${i == 0 ? 'indicator-worst': i == fctTable.indicators.size()-1 ? 'indicator-best': 'indicator-middle'}"
										data-average="${reportBarAverage}"
										data-indicator="${indicator.code}"
										title="${reportBarTooltip(percentage: reportBarPercentage, value: reportBarValue, totalLocations: fctTable.getTableReportValue(location, indicator)?.getNumberOfDataLocations())}"
										style="height: ${reportBarPercentage};">
										
										<g:if test="${reportBarAverage > 0.06}">
											<span data-average="${reportBarAverage}" data-percentage="${reportBarPercentage}">
											${reportBarValue}
											</span>
										</g:if>
										
									</div>
								</g:if>
								<g:else>
									<div class="js_bar_vertical tooltip"
										data-percentage="N/A" title="${message(code:'fct.report.table.na')}" 
										style="height: 0%;"></div>
								</g:else>
							</g:each>
						</div>
						<g:if test="${totalAverage > 0 && totalAverage != 1}">
							<r:script>updateStackedBarHeight(${totalAverage}, ${maxAverage}, "${location.code}", "${maxIndicator}")</r:script>
						</g:if>
					</g:if>
				</td>
			</g:each>
		</tr>
		<r:script>
		function updateStackedBarHeight(totalBarAverage, maxBarAverage, location, indicator){
			if(totalBarAverage > 0 && totalBarAverage != 1){
				if(totalBarAverage < 1) maxBarAverage += 1-totalBarAverage
				else if(totalBarAverage > 1) maxBarAverage -= totalBarAverage-1
				var stackedBars = $('.bars-vertical[data-location="'+location+'"]')
				var maxStackedBar = $(stackedBars).children('.bar-vertical[data-indicator="'+indicator+'"]')
				$(maxStackedBar).css('height', maxBarAverage*100 + '%')
			}
		}
		</r:script>
		<!-- chart x-axis -->
		<tr>
			<g:if test="${fctTable != null && fctTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes).empty}">
				<g:each in="${fctTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes)}" var="location">
					<td>
						<g:if test="${!location.collectsData()}">
							<% def childLocationLinkParams = new HashMap(params) %>
							<% childLocationLinkParams['location'] = location.id+"" %>
							<a href="${createLink(controller:controllerName, action:actionName, params:childLocationLinkParams)}">
							<g:i18n field="${location.names}" /></a>
						</g:if>
						<g:else>
							<g:i18n field="${location.names}" />
						</g:else>
					</td>
				</g:each>
			</g:if>
		</tr>
	</tbody>
</table>