<table class="nested push-top-10 ${fctTable.targetOptions.size() > 3 ? 'col4' : ''}">
	<thead>
		<tr>
			<th>
				<a class="expand-all" href="#"><g:message code="report.program.table.tree.expandall"/></a> 
				<a class="collapse-all" href="#"><g:message code="report.program.table.tree.collapseall"/></a>
				&nbsp;
				<g:if test="${currentLocation.parent != null}">
	               	<% def parentLocationLinkParams = new HashMap(params) %>
					<% parentLocationLinkParams['location'] = currentLocation.parent?.id+"" %>					
					<a class="level-up" href="${createLink(controller:'dsr', action:'view', params:parentLocationLinkParams)}">
					<g:message code="report.view.label" args="${[i18n(field: currentLocation.parent.names)]}"/></a>							  
				</g:if>			
			</th>
			<g:if test="${fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
				<g:each in="${fctTable.targetOptions}" var="targetOption">
					<th>						
						<g:i18n field="${targetOption.names}" />
						<g:render template="/templates/help_tooltip" 
							model="[names: i18n(field: targetOption.names), descriptions: i18n(field: targetOption.descriptions)]" />
					</th>
				</g:each>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:render template="/fct/reportProgramTableTree" model="[location:currentLocation, level:0]"/>				
	</tbody>			
</table>