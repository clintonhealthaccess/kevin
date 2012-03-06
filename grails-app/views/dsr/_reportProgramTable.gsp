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
		<g:render template="/dsr/reportProgramTableTree" model="[location:currentLocation, level:0]"/>				
	</tbody>			
</table>