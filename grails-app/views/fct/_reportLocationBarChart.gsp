<!-- chart scale -->
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
								<g:set var="totalAverage" value="${fctTable.getTotalAverage(location)}" />
								<div class="bars-vertical" data-total-average="${totalAverage}" 
										style="margin-bottom:${totalAverage > 0 && totalAverage < 1 ? ((totalAverage-1)*200).round() : 0}px;">
									<g:each in="${fctTable.targetOptions.reverse()}" var="targetOption" status="i">
										<g:if test="${!fctTable.getReportValue(location, targetOption).getAverage().isNull()}">																						
											<g:set var="reportAverage" value="${g.reportValue(value: fctTable.getReportValue(location, targetOption).getAverage(), type: targetOption.sum.type, format: '#%')}"/>
											<g:set var="reportValue" value="${g.reportValue(value: fctTable.getReportValue(location, targetOption).getValue(), type: targetOption.sum.type)}"/>
											<div class="js_bar_vertical bar-vertical tooltip bar${fctTable.targetOptions.size()-i}"
												data-percentage="${reportAverage}"
												title="${reportTooltip(average: reportAverage, value: reportValue, 
														totalLocations: fctTable.getReportValue(location, targetOption).getNumberOfDataLocations())}"															
												style="height: ${reportAverage};">
												<g:if test="${reportValue != '0'}">
													<span>${reportValue}</span>
												</g:if>												
											</div>
										</g:if>
										<g:else>
											<div class="js_bar_vertical tooltip bar${i+1}"
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
		<tr>
			<g:if test="${fctTable != null && fctTable.topLevelLocations != null && !fctTable.topLevelLocations.empty}">
				<g:each in="${fctTable.topLevelLocations}" var="location">
					<td><g:i18n field="${location.names}" /></td>
				</g:each>
			</g:if>
		</tr>
	</tbody>
</table>