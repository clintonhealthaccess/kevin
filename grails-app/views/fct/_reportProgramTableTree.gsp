<g:if test="${location.collectsData()}">
	<!-- DataLocations -->
	<tr>
		<td>
			<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
		</td>
		<g:each in="${fctTable.targetOptions}" var="targetOption">
			<td>
				<g:if test="${fctTable.getReportValue(location, targetOption) != null && !fctTable.getReportValue(location, targetOption).getValue().isNull()}">
					<div class="report-datalocation-value">
						<g:reportValue value="${fctTable.getReportValue(location, targetOption).getValue()}" type="${targetOption.sum.type}"/>
					</div>					
				</g:if>
				<g:else>
					<div class="report-value-na"><g:message code="report.value.na"/></div>
				</g:else>
			</td>
		</g:each>
	</tr>
</g:if>
<g:else>
	<!-- Locations -->
	<g:if test="${locationTree != null && !locationTree.empty && locationTree.contains(location)}">
		<tr class="tree-sign js_foldable">
			<td class="js_foldable-toggle ${location.id == currentLocation.id ? 'toggled': ''}">
				<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
			</td>
			<g:each in="${fctTable.targetOptions}" var="targetOption">
				<td>
					<g:if test="${fctTable.getReportValue(location, targetOption) != null}">
						<g:if test="${fctTable.getReportValue(location, targetOption).getValue() != null 
							&& !fctTable.getReportValue(location, targetOption).getValue().isNull()}">
							<div class="report-location-value">
								<g:reportValue value="${fctTable.getReportValue(location, targetOption).getValue()}" type="${targetOption.sum.type}"/>
							</div>
						</g:if>
						<g:if test="${fctTable.getReportValue(location, targetOption).getAverage() != null 
							&& !fctTable.getReportValue(location, targetOption).getAverage().isNull()}">
							<div class="report-location-percentage hidden">
								<g:reportPercentage value="${fctTable.getReportValue(location, targetOption).getAverage()}"/>
							</div>
						</g:if>
					</g:if>
					<g:else>
						<div class="report-value-na"><g:message code="report.value.na"/></div>
					</g:else>
				</td>
			</g:each>
		</tr>
		<tr class="sub-tree js_foldable-container hidden"
			style="display:${location.id == currentLocation.id ? 'table-row': 'none'};">
			<td class="bucket" colspan="${fctTable.targetOptions.size()+1}">				
				<table>
					<tbody>					
						<g:each in="${location.getAllChildren(locationSkipLevels, currentLocationTypes)}" var="child">	
							<g:render template="/fct/reportProgramTableTree" model="[location:child, level:level+1]"/>
						</g:each>
					</tbody>
				</table>			
			</td>
		</tr>
	</g:if>
</g:else>