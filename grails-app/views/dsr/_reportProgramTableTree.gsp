<g:if test="${location.collectsData()}">
	<!-- DataLocations -->
		<tr>
			<td>
				<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
			</td>
			<g:if test="${dsrTable.targets != null && !dsrTable.targets.empty}">
				<g:each in="${dsrTable.targets}" var="target">
					<td>
						<g:if test="${dsrTable.getReportValue(location, target) != null}">
							<g:reportValue value="${dsrTable.getReportValue(location, target)}" type="${target.data.type}" format="${target.format}"/>
						</g:if>
						<g:else>
							<div class="report-value-na"><g:message code="report.value.na"/></div>
						</g:else>
					</td>
				</g:each>
			</g:if>
		</tr>
</g:if>
<g:else>
	<!-- Locations -->
	<g:if test="${locationTree != null && !locationTree.empty && locationTree.contains(location)}">
		<tr class="tree-sign js_foldable">
			<td class="js_foldable-toggle ${location.id == currentLocation.id ? 'toggled': ''}">
				<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
			</td>
			<g:if test="${dsrTable.targets != null && !dsrTable.targets.empty}">
				<g:each in="${dsrTable.targets}" var="target">
					<td>
						<g:if test="${dsrTable.getReportValue(location, target) != null}">						
							<g:reportValue value="${dsrTable.getReportValue(location, target)}" type="${target.data.type}" format="${target.format}"/>
						</g:if>
					</td>
				</g:each>
			</g:if>
		</tr>
		<tr class="sub-tree js_foldable-container hidden"
			style="display:${location.id == currentLocation.id ? 'table-row': 'none'};">
			<td class="bucket" colspan="${dsrTable.targets.size()+1}">				
				<table>
					<tbody>
						<g:each in="${location.getAllChildren(locationSkipLevels, currentLocationTypes)}" var="child">	
							<g:render template="/dsr/reportProgramTableTree" model="[location:child, level:level+1]"/>
						</g:each>
					</tbody>
				</table>			
			</td>
		</tr>
	</g:if>
</g:else>