<g:if test="${locationTree != null && !locationTree.empty && locationTree.contains(location)}">
	<g:if test="${location.getChildren(skipLevels) == null || location.getChildren(skipLevels).empty}">
		<tr>
			<td>
				<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
			</td>
			<g:each in="${fctTable.targetOptions}" var="targetOption">
				<td>
					<g:if test="${fctTable.getReportValue(location, targetOption) != null}">
						<g:reportValue value="${fctTable.getReportValue(location, targetOption)}" type="${targetOption.sum.type}" format="${targetOption.format}"/>
					</g:if>
					<g:else>
						<div class="report-value-na"><g:message code="report.value.na"/></div>
					</g:else>
				</td>
			</g:each>
		</tr>
	</g:if>
	<g:else>
		<tr class="tree-sign js_foldable">
			<td class="js_foldable-toggle ${location.id == currentLocation.id ? 'toggled': ''}">
				<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
			</td>
			<g:each in="${fctTable.targetOptions}" var="targetOption">
				<td>
					<g:if test="${fctTable.getReportValue(location, targetOption) != null}">
						${fctTable.getReportValue(location, targetOption).value}
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
						<g:each in="${location.getChildren(skipLevels)}" var="child">	
							<g:render template="/fct/reportProgramTableTree" model="[location:child, level:level+1]"/>
						</g:each>
					</tbody>
				</table>			
			</td>
		</tr>
	</g:else>
</g:if>