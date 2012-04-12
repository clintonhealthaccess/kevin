<!-- chart scale -->
<g:set var="yMax" value="${fctTable.getMaxReportValue().intValue()}"/>
<ul class="chart">
	<li>${yMax}</li>
	<li>${yMax/2}</li>
	<li>0</li>
</ul>
<!-- chart -->
<table class="vertical-graph">
	<tbody>
		<tr>
			<g:if test="${fctTable != null && fctTable.topLevelLocations != null && !fctTable.topLevelLocations.empty}">
				<g:each in="${fctTable.topLevelLocations}" var="location">
						<td>
							<g:if test="${fctTable != null && fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
								<g:each in="${fctTable.targetOptions}" var="targetOption" status="i">
									<g:if test="${fctTable.getReportValue(location, targetOption) != null}">
										<g:set var="reportValue" value="${fctTable.getReportValue(location, targetOption).numberValue}" />
										<div class="js_bar_vertical tooltip bar${i+1}"
											data-percentage="${reportValue}" title="${reportValue}"
											style="height: ${(reportValue/yMax)*100}%;"></div>
									</g:if>
									<g:else>
										<div class="js_bar_vertical tooltip bar${i+1}"
											data-percentage="N/A" title="${message(code:'fct.report.table.na')}" 
											style="height: 0%;"></div>
									</g:else>
								</g:each>
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
<!-- chart legend -->
<ul class="chart_legend">
	<g:if test="${fctTable != null && fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
		<g:each in="${fctTable.targetOptions}" var="targetOption" status="i">
			<li><span class="bar${i+1}"></span> <g:i18n
					field="${targetOption.names}" /></li>
		</g:each>
	</g:if>
</ul>