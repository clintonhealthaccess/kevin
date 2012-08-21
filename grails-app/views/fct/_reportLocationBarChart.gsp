<!-- chart legend -->
<ul class="horizontal chart_legend">
	<g:if test="${fctTable != null && fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
		<g:each in="${fctTable.targetOptions}" var="targetOption" status="i">
			<li>
			<span class="${i == fctTable.targetOptions.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}"></span>
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
			<g:if test="${fctTable != null && fctTable.topLevelLocations != null && !fctTable.topLevelLocations.empty}">
				<g:each in="${fctTable.topLevelLocations}" var="location">
						<td>
							<g:if test="${fctTable != null && fctTable.targetOptions != null && !fctTable.targetOptions.empty}">								
								<!-- total report average between 0 and 1 -->
								<g:set var="totalAverage" value="${fctTable.getTotalAverage(location)}" />											
								<div class="bars-vertical" data-total-average="${totalAverage}" style="margin-bottom:${totalAverage > 0 && totalAverage < 1 ? ((totalAverage-1)*200).round() : 0}px;">									
									<g:each in="${fctTable.targetOptions.reverse()}" var="targetOption" status="i">
										<g:set var="value" value="${fctTable.getReportValue(location, targetOption)}"/>									
										<g:if test="${value != null && value.getValue() != null && !value.getValue().isNull() && value.getAverage() != null && !value.getAverage().isNull()}">											
											<!-- report value -->
											<g:set var="reportValue" value="${g.reportValue(value: fctTable.getReportValue(location, targetOption).getValue(), type: targetOption.type, format: targetOption.format)}"/>											
											<!-- report value between 0 and 1 -->
											<g:set var="reportAverage" value="${fctTable.getReportValue(location, targetOption).getAverage().numberValue.round(2)}"/>
											<!-- report value between 0% and 100% -->
											<g:set var="reportPercentage" value="${g.reportPercentage(value: fctTable.getReportValue(location, targetOption).getAverage())}"/>
											<!-- report total data locations -->
											<g:set var="totalDataLocations" value="${fctTable.getReportValue(location, targetOption).getNumberOfDataLocations()}"/>											
											<!-- stacked bar -->
											<div class="js_bar_vertical bar-vertical tooltip ${i == 0 ? 'indicator-worst': i == fctTable.targetOptions.size()-1 ? 'indicator-best': 'indicator-middle'}"
												data-percentage="${reportPercentage}"
												title="${reportTooltip(average: reportPercentage, value: reportValue, totalLocations: totalDataLocations)}"
												style="height: ${reportPercentage};">
												<g:if test="${reportAverage > 0.06}">
													<span data-average="${reportAverage}" data-percentage="${reportPercentage}">${reportValue}</span>
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
							</g:if>
						</td>
				</g:each>
			</g:if>
		</tr>
		<!-- chart x-axis -->
		<tr>
			<g:if test="${fctTable != null && fctTable.topLevelLocations != null && !fctTable.topLevelLocations.empty}">
				<g:each in="${fctTable.topLevelLocations}" var="location">
					<td><g:i18n field="${location.names}" /></td>
				</g:each>
			</g:if>
		</tr>
	</tbody>
</table>