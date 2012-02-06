<g:if test="${location.collectsData()}">
	<tr>
	  <td>
	    <span style='margin-left: ${level*20}px;'><g:i18n field="${location.names}"/></span>
	  </td>
		<g:each in="${dsrTable.targets}" var="target">
			<td class="dsr-target" data-category="${target.category?.id ?: 0}">
				<g:if test="${dsrTable.getReportValue(location, target) != null}">
					${dsrTable.getReportValue(location, target).value}
				</g:if>
			</td>
		</g:each>
	</tr>	
</g:if>
<g:else>
	<tr class="tree_sign_plus">
		<td><span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span></td>
		<g:each in="${dsrTable.targets}" var="target">
			<td class="dsr-target" data-category="${target.category?.id ?: 0}"></td>
		</g:each>
	</tr>
	<tr class="sub_tree">
		<td class="bucket" colspan="${dsrTable.targets.size()+1}">					
			<table>
				<tbody>
					<g:if test="${location.children != null && !location.children.empty}">
						<g:each in="${location.children}" var="child">
							<g:render template="/templates/dsr/reportProgramTableTree"
							model="[location:child, level:level+1, params:params]"/>
						</g:each>
					</g:if>
					<g:if test="${location.dataLocationEntities != null && !location.dataLocationEntities.empty}">
						<g:each in="${location.dataLocationEntities}" var="entity">
							<g:render template="/templates/dsr/reportProgramTableTree"
							model="[location:entity, level:level+1, params:params]"/>
						</g:each>
					</g:if>
				</tbody>
			</table>			
		</td>
	</tr>	
</g:else>