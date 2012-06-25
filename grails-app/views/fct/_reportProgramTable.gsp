<table class="nested push-top-10">
	<thead>
		<tr>
			<th>
				<g:message code="fct.report.table.location"/>
				<a class="expand-all" href="#"><g:message code="report.program.table.tree.expandall"/></a> 
				<a class="collapse-all" href="#"><g:message code="report.program.table.tree.collapseall"/></a>
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