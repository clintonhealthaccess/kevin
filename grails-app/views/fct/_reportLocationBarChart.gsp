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
								<div class="js_bars_vertical">
									<g:each in="${fctTable.targetOptions.reverse()}" var="targetOption" status="i">									
										<g:if test="${!fctTable.getReportValue(location, targetOption).getAverage().isNull()}">
											<g:set var="reportValue" value="${fctTable.getReportValue(location, targetOption).getAverage().numberValue * 100}"/>									
											<div class="js_bar_vertical tooltip bar${fctTable.targetOptions.size()-i}"
												data-percentage="${reportValue}" title="${i18n(field: targetOption.names) + ': ' + reportValue}%"
												style="height: ${reportValue}%;" onload="$(this).tipsy('show'); return false;"><span>${reportValue}</span></div>
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