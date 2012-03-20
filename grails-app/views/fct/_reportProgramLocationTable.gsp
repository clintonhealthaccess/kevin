<table class="listing push-top-10 push-20">
	<thead>
		<tr>
			<th>Location</th>
			<g:if test="${fctTable != null && fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
				<g:each in="${fctTable.targetOptions}" var="targetOption">
					<th><g:i18n field="${targetOption.names}" /></th>
				</g:each>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:if test="${fctTable != null && fctTable.locations != null && !fctTable.locations.empty}">			
			<g:each in="${fctTable.locations}" var="location">
				<tr>
				   <td><g:i18n field="${location.names}" /></td>
				   <g:if test="${fctTable != null && fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
					   <g:each in="${fctTable.targetOptions}" var="targetOption">
							<g:if test="${!fctTable.getReportValue(location, targetOption) != null}">
								<td>${fctTable.getReportValue(location, targetOption).value}</td>
							</g:if>
							<g:else>N/A</g:else>
						</g:each>
					</g:if>
				 </tr>
			 </g:each>
		</g:if>			
		<tr>
		   <td><g:i18n field="${currentLocation.names}" /></td>
		   <g:if test="${fctTable != null && fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
			   <g:each in="${fctTable.targetOptions}" var="targetOption">
					<g:if test="${!fctTable.getTotalValue(targetOption) != null}">
						<td>${fctTable.getTotalValue(targetOption).value}</td>
					</g:if>
					<g:else><td>N/A</td></g:else>
				</g:each>
			</g:if>
		 </tr>		
	</tbody>
</table>