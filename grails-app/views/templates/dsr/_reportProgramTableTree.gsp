<g:if test="${location.collectsData()}">
	<tr>
	  <td>
	    <span style='margin-left: 40px;'><g:i18n field="${location.names}"/></span>
	  </td>
		<g:each in="${dsrTable.targets}" var="target">
			<g:if test="${!dsrTable.getReportValue(location, target) != null}">
				<td>${dsrTable.getReportValue(child, target).value}</td>
			</g:if>
		</g:each>
	</tr>
</g:if>
<g:else>
	<tr ${dsrTable.topLevelLocations.contains(location) ? 'class="tree_sign_minus"' : 'class="tree_sign_plus"'}>
		<td><span><g:i18n field="${location.names}"/></span></td>
		<g:each in="${dsrTable.targets}" var="target">
			<td></td>
		</g:each>
	</tr>
	<tr class='sub_tree' style='display: table-row'>
		<td class='bucket' colspan="${dsrTable.targets.size()+1}">		
			<g:if test="${location.children != null && !location.children.empty}">
				<table>
					<g:each in="${location.children}" var="child">
						<g:render template="/templates/dsr/reportProgramTableTree"
						model="[location:child, params:params]"/>
					</g:each>
				</table>
			</g:if>			
		</td>
	</tr>
</g:else>