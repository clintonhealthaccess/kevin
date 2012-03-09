<table class="listing push-top-10 push-20">
	<thead>
		<tr>
			<th>Location</th>
			<g:if test="${fctTable != null && fctTable.targets != null && !fctTable.targets.empty}">
				<g:each in="${fctTable.targets}" var="target">
					<th><g:i18n field="${target.names}" /></th>
				</g:each>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:if test="${fctTable != null && fctTable.locations != null && !fctTable.locations.empty}">			
			<g:each in="${fctTable.locations}" var="location">
				<tr>
				   <td><g:i18n field="${location.names}" /></td>
				   <g:if test="${fctTable != null && fctTable.targets != null && !fctTable.targets.empty}">
					   <g:each in="${fctTable.targets}" var="target">
							<g:if test="${!fctTable.getReportValue(location, target) != null}">
								<td>${fctTable.getReportValue(location, target).value}</td>
							</g:if>
							<g:else>N/A</g:else>
						</g:each>
					</g:if>
				 </tr>
			 </g:each>
		</g:if>			
		<tr>
		   <td><g:i18n field="${currentLocation.names}" /></td>
		   <g:if test="${fctTable != null && fctTable.targets != null && !fctTable.targets.empty}">
			   <g:each in="${fctTable.targets}" var="target">
					<g:if test="${!fctTable.getTotalValue(target) != null}">
						<td>${fctTable.getTotalValue(target).value}</td>
					</g:if>
					<g:else><td>N/A</td></g:else>
				</g:each>
			</g:if>
		 </tr>		
	</tbody>
</table>