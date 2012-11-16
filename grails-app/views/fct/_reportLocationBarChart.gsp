<!-- chart legend -->
<ul class="horizontal chart_legend">
	<g:if test="${fctTable != null && fctTable.indicators != null && !fctTable.indicators.empty}">
		<g:each in="${fctTable.indicators}" var="targetOption" status="i">
			<li>
			<span class="${i == fctTable.indicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}"></span>
			<g:i18n field="${targetOption.names}" /></li>
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
						
						<!-- total report averages for a location --><g:set var="reportAverages" value="${fctTable.get(location).values.collect{it?.getAverage()}}" />
						<!-- total report average between 0 and ~100 --><g:set var="totalAverage" value="${g.reportBarTotalPercentage(values: reportAverages, type: currentTarget.type, format: '#%', rounded: '2')}"/>
																	
						<div class="bars-vertical" data-total-average="${totalAverage}" style="margin-bottom:${(((totalAverage/100)-1)*200).round()}px;">
							<g:each in="${fctTable.indicators.reverse()}" var="targetOption" status="i">
								<g:set var="value" value="${fctTable.getTableReportValue(location, targetOption)}"/>									
								<g:if test="${value != null && !value.value.isNull() && !value.average.isNull()}">											
									
									<!-- report value number --><g:set var="reportBarValue" value="${g.reportBarData(value: fctTable.getTableReportValue(location, targetOption)?.getValue(), type: targetOption.type)}"/>											
									<!-- report value number 0-1 --><g:set var="reportBarAverage" value="${g.reportBarData(value: fctTable.getTableReportValue(location, targetOption)?.getAverage(), type: targetOption.type, format: '#.##', rounded: '2'}"/>
									<!-- report value percentage 0%-100% --><g:set var="reportBarPercentage" value="${g.reportBarPercentage(values: reportAverages, value: fctTable.getTableReportValue(location, targetOption)?.getAverage(), type: targetOption.type, format: targetOption.percentageFormat?:'#%', rounded: '2')}"/>
									
									<!-- stacked bar -->
									<div class="js_bar_vertical bar-vertical tooltip ${i == 0 ? 'indicator-worst': i == fctTable.indicators.size()-1 ? 'indicator-best': 'indicator-middle'}"
										title="${reportBarTooltip(percentage: reportBarPercentage, value: reportBarValue, totalLocations: fctTable.getTableReportValue(location, targetOption)?.getNumberOfDataLocations())}"
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
							<r:script>
								var barAverages = null 
								barAverages = $('bar-vertical').map(function() {
								  return this.data('average');
								}).get().join(',');
							</r:script>
						</div>
					</g:if>
				</td>
			</g:each>
		</tr>
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