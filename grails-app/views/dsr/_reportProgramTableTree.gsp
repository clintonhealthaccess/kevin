<g:if test="${location.collectsData()}">
	<!-- DataLocations -->
	<tr>
		<td>
			<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
		</td>
		<g:each in="${dsrTable.indicators}" var="target">
			<td>
				<g:reportValue
					tooltip="${i18n(field: target.names)}"
					value="${dsrTable.getTableReportValue(location, target)?.value}" 
					type="${target.data.type}" 
					format="${target.format}"/>	
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
			<g:each in="${dsrTable.indicators}" var="target">
				<td>
					<g:if test="${location.getChildren(locationSkipLevels) == null || location.getChildren(locationSkipLevels).empty}">
						<g:if test="${dsrTable.getTableReportValue(location, target) != null}">
							<g:reportValue
								tooltip="${i18n(field: target.names)}"
								value="${dsrTable.getTableReportValue(location, target)?.value}"
								listValue="${dsrTable.getModeList(location, target)}"
								type="${target.data.type}"
								format="${target.format}"/>
						</g:if>
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