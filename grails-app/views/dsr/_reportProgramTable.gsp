<table class="nested push-top-10">
	<thead>
		<tr>
			<th><g:message code="dsr.report.table.location"/>
				<a class="expand-all" href="#"><g:message code="report.program.table.tree.expandall"/></a> 
				<a class="collapse-all" href="#"><g:message code="report.program.table.tree.collapseall"/></a>
			</th>
			<g:if test="${dsrTable.targets != null && !dsrTable.targets.empty}">
				<g:each in="${dsrTable.targets}" var="target">
					<th>
						<g:i18n field="${target.names}" />
						<g:render template="/templates/help_tooltip" 
							model="[names: i18n(field: target.names), descriptions: i18n(field: target.descriptions)]" />
					</th>
				</g:each>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:render template="/dsr/reportProgramTableTree" model="[location:currentLocation, level:0]"/>				
	</tbody>			
</table>