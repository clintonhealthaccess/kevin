<table class='nested push-top-10'>
	<thead>
		<tr>
			<th>Facility</th>
			<g:if test="${dsrTable.targets != null && !dsrTable.targets.empty}">
				<g:each in="${dsrTable.targets}" var="target">
					<th><g:i18n field="${target.names}" /></th>
				</g:each>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:if test="${dsrTable.topLevelLocations != null && !dsrTable.topLevelLocations.empty}">
			<g:each in="${dsrTable.topLevelLocations}" var="topLevelLocation">
				<g:render template="/templates/dsr/reportProgramTableTree" model="[location:topLevelLocation, level: 0, params:params]"/>
			</g:each>
		</g:if>
	</tbody>
</table>