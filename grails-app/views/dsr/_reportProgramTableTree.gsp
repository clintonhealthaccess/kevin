<g:if test="${location.collectsData()}">
	<g:if test="${currentLocationTypes != null && !currentLocationTypes.empty && currentLocationTypes.contains(location.type)}">
		<tr>
		  <td>
		    <span style='margin-left: ${level*20}px;'><g:i18n field="${location.names}"/></span>
		  </td>
			<g:each in="${dsrTable.targets}" var="target">
				<td>
					<g:if test="${dsrTable.getReportValue(location, target) != null}">
						${dsrTable.getReportValue(location, target).value}
					</g:if>
				</td>
			</g:each>
		</tr>
	</g:if>
</g:if>
<g:else>
	<g:if test="${locationTree != null && !locationTree.empty && locationTree.contains(location)}">
		<tr class="tree_sign_plus" >
			<td><span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span></td>
			<g:each in="${dsrTable.targets}" var="target">
				<td></td>
			</g:each>
		</tr>
		<tr class="sub_tree" style="display:none;">
			<td class="bucket" colspan="${dsrTable.targets.size()+1}">				
				<table>
					<tbody>
						<g:if test="${location.children != null && !location.children.empty}">							
							<g:each in="${location.getChildrenEntities(skipLevels, currentLocationTypes)}" var="child">	
								<g:if test="${locationTree.contains(location) || dataLocationTree.contains(location)}">						
									<g:render template="/dsr/reportProgramTableTree"
									model="[location:child, level:level+1, locationTree:locationTree, dataLocationTree:dataLocationTree]"/>
								</g:if>							
							</g:each>
						</g:if>
						<g:if test="${location.dataLocationEntities != null && !location.dataLocationEntities.empty}">							
							<g:each in="${location.dataLocationEntities}" var="entity">
								<g:render template="/dsr/reportProgramTableTree"
								model="[location:entity, level:level+1]"/>
							</g:each>
						</g:if>
					</tbody>
				</table>			
			</td>
		</tr>
	</g:if>
</g:else>