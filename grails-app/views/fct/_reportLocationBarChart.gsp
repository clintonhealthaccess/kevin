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
									
									<!-- report value number -->
									<g:set var="reportBarValue" value="${g.reportBarData(value: fctTable.getTableReportValue(location, indicator)?.getValue(), type: indicator.type)}"/>											
									<!-- report value number 0-1 -->
									<g:set var="reportBarAverage" value="${fctTable.getTableReportValue(location, indicator)?.getAverage().numberValue.round(2)}"/>
									<!-- report value percentage 0%-100% -->
									<g:set var="reportBarPercentage" value="${g.reportBarData(value: fctTable.getTableReportValue(location, indicator)?.getAverage(), type: indicator.type, format: indicator.percentageFormat?:'#%', rounded: '2')}"/>

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
							<g:if test="${totalAverage > 0 && totalAverage != 1}">
								<input type="hidden" data-location="${location.code}" name="maxIndicator" value="${maxIndicator}"/>
								<input type="hidden" data-location="${location.code}" name="maxAverage" value="${maxAverage}"/>
								<input type="hidden" data-location="${location.code}" name="totalAverage" value="${totalAverage}"/>
							</g:if>
						</div>
					</g:if>
				</td>
			</g:each>
		</tr>
		<r:script>
		$(document).ready(function() {
			var stackedBars = $('.bars-vertical')
			$(stackedBars).each(function(index, stackedBar){
				var code = $(stackedBar).data('location')
				if($(':hidden[data-location="'+code+'"]').size() > 0){
					var totalBarAverage = parseFloat($(':hidden[data-location="'+code+'"][name="totalAverage"]').val())
					if(totalBarAverage > 0 && totalBarAverage != 1){
						var maxBarAverage = parseFloat($(':hidden[data-location="'+code+'"][name="maxAverage"]').val())
						if(totalBarAverage < 1) maxBarAverage += 1-totalBarAverage
						else if(totalBarAverage > 1) maxBarAverage -= totalBarAverage-1
						var maxIndicator = $(':hidden[data-location="'+code+'"][name="maxIndicator"]').val()
						var maxStackedBar = $(stackedBar).children('.bar-vertical[data-indicator="'+maxIndicator+'"]')
						$(maxStackedBar).css('height', maxBarAverage*100 + '%')
					}
				}
			})
		});
		</r:script>
		<!-- chart x-axis -->
		<tr>
			<g:each in="${fctTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes)}" var="location">
				<td>
					<g:if test="${!location.collectsData() && fctTable.hasData(location)}">
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
		</tr>
	</tbody>
</table>