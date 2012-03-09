<!-- chart scale -->
<ul class="chart">
	<li>100</li>
	<li>50</li>
	<li>0</li>
</ul>
<!-- chart -->
<table class="vertical-graph">
	<tbody>
		<tr>
			<g:if test="${fctTable != null && fctTable.locations != null && !fctTable.locations.empty}">
				<g:each in="${fctTable.locations}" var="location">
					<td>
						<g:if test="${fctTable != null && fctTable.targets != null && !fctTable.targets.empty}">
							<g:each in="${fctTable.targets}" var="target" status="i">
								<g:if test="${!fctTable.getReportValue(location, target) != null}">
									<g:set var="reportValue" value="${fctTable.getReportValue(location, target).value}" />
									<div class="js_bar_vertical tooltip bar${i+1}"
										data-percentage="${reportValue}" title="${reportValue}%"
										style="height: ${reportValue}%;"></div>
								</g:if>
								<g:else>
									<div class="js_bar_vertical tooltip bar${i+1}"
										data-percentage="N/A" title="N/A" style="height: 0%;"></div>
								</g:else>
							</g:each>
						</g:if>
					</td>
				</g:each>
			</g:if>
			<td>
				<g:if test="${fctTable != null && fctTable.targets != null && !fctTable.targets.empty}">
					<g:each in="${fctTable.targets}" var="target" status="i">
						<g:if test="${!fctTable.getTotalValue(target) != null}">
							<g:set var="totalValue" value="${fctTable.getTotalValue(target).value}" />
							<div class="js_bar_vertical tooltip bar${i+1}"
								data-percentage="${totalValue}" title="${totalValue}%"
								style="height: ${totalValue}%;"></div>
						</g:if>
						<g:else>
							<div class="js_bar_vertical tooltip bar${i+1}"
								data-percentage="N/A" title="N/A" style="height: 0%;"></div>
						</g:else>
					</g:each>
				</g:if>
				</td>				
		</tr>
		<tr>
			<g:if test="${fctTable != null && fctTable.locations != null && !fctTable.locations.empty}">
				<g:each in="${fctTable.locations}" var="location">
					<td><g:i18n field="${location.names}" /></td>
				</g:each>
			</g:if>
			<td><g:i18n field="${currentLocation.names}" /></td>
		</tr>
	</tbody>
</table>
<!-- chart legend -->
<ul class="chart_legend">
	<g:if test="${fctTable != null && fctTable.targets != null && !fctTable.targets.empty}">
		<g:each in="${fctTable.targets}" var="target" status="i">
			<li><span class="bar${i+1}"></span> <g:i18n
					field="${target.names}" /></li>
		</g:each>
	</g:if>
</ul>