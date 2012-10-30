<g:if test="${location.collectsData()}">
	<!-- DataLocations -->
	<tr>
		<td>
			<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
		</td>
		<g:each in="${fctTable.targetOptions}" var="targetOption">
			<td>
				<g:reportValue
					tooltip="${i18n(field: targetOption.names)}"
					value="${fctTable.getTableReportValue(location, targetOption).getValue()}" 
					type="${targetOption.sum.type}"/>					
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
					<g:if test="${fctTable.getTableReportValue(location, targetOption) != null}">
						<div class="report-value-number">
							<g:reportValue
								tooltip="${i18n(field: targetOption.names)}" 
								value="${fctTable.getTableReportValue(location, targetOption).getValue()}" 
								type="${targetOption.type}" 
								format="${targetOption.numberFormat}"/>
						</div>
						<div class="report-value-percentage hidden">
							<g:reportPercentage
								tooltip="${i18n(field: targetOption.names)}"
								value="${fctTable.getTableReportValue(location, targetOption).getAverage()}" 
								type="${targetOption.type}" 
								format="${targetOption.percentageFormat}"/>
						</div>
					</g:if>
					<g:else>
						<span class="tooltip" original-title="${i18n(field: targetOption.names)}">
							<div class="report-value-na"><g:message code="report.value.na"/></div>
						</span>
					</g:else>
				</td>
			</g:each>
		</tr>
		<tr class="sub-tree js_foldable-container hidden"
			style="display:${location.id == currentLocation.id ? 'table-row': 'none'};">
			<td class="bucket" colspan="${fctTable.targetOptions.size()+1}">				
				<table>
					<tbody>					
						<g:each in="${location.getChildrenLocations(locationSkipLevels, currentLocationTypes)}" var="child">	
							<g:render template="/fct/reportProgramTableTree" model="[location:child, level:level+1]"/>
						</g:each>
					</tbody>
				</table>			
			</td>
		</tr>
	</g:if>
</g:else>