<table class="nested push-top-10">
	<thead>
		<tr>
			<th>Facility</th>
			<g:if test="${fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
				<g:each in="${fctTable.targetOptions}" var="targetOption">
					<th><g:i18n field="${targetOption.names}" /></th>
				</g:each>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:render template="/fct/reportProgramTableTree" model="[location:currentLocation, level:0]"/>				
	</tbody>			
</table>