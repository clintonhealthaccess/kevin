<g:if test="${location.collectsData()}">
	<!-- DataLocations -->
	<tr>
		<td>
			<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
		</td>
		<g:each in="${dsrTable.indicators}" var="indicator">
			<td>
				<g:reportValue
					tooltip="${i18n(field: indicator.names)}"
					value="${dsrTable.getTableReportValue(location, indicator)?.value}" 
					type="${indicator.data.type}" 
					format="${indicator.format}"/>
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
			<g:each in="${dsrTable.indicators}" var="indicator">
				<td>
					<g:set var="tableValue" value="${dsrTable.getTableReportValue(location, indicator)}"/>
					<g:if test="${tableValue != null && !tableValue.isNull()}">
						<g:reportValue
							tooltip="${i18n(field: indicator.names)}"
							value="${indicator.average ? tableValue?.average : tableValue?.value}"
							listValue="${dsrTable.getModeList(location, indicator)}"
							type="${indicator.data.type}"
							format="${indicator.format}"/>
					</g:if>
				</td>
			</g:each>
		</tr>
		<tr class="sub-tree js_foldable-container hidden"
			style="display:${location.id == currentLocation.id ? 'table-row': 'none'};">
			<td class="bucket" colspan="${dsrTable.indicators.size()+1}">				
				<table>
					<tbody>
						<g:each in="${dsrTable.getLocations(location, locationSkipLevels, currentLocationTypes)}" var="child">	
							<g:render template="/dsr/reportProgramTableTree" model="[location:child, level:level+1]"/>
						</g:each>
					</tbody>
				</table>			
			</td>
		</tr>
	</g:if>
</g:else>