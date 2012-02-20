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
		<g:if test="${currentLocation.children != null && !currentLocation.children.empty}">			
			<g:each in="${currentLocation.children}" var="child" status="i">						
				<g:render template="/dsr/reportProgramTableTree"
				model="[location:child, first:i, level:0, locationTree:locationTree]"/>				
			</g:each>
		</g:if>
		<g:if test="${currentLocation.dataLocationEntities != null && !currentLocation.dataLocationEntities.empty}">
			<g:each in="${currentLocation.dataLocationEntities}" var="entity">
				<g:render template="/dsr/reportProgramTableTree"
				model="[location:entity, level:0, locationTree:locationTree]"/>
			</g:each>
		</g:if>
	</tbody>			
</table>