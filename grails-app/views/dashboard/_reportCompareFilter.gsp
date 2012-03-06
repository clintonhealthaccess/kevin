<div class="selector right">
	<span>Compare</span>
	<g:form name="${table}-form" method="get"
		url="${[controller:'dashboard', action:'compare',
			params:[table:table, period:currentPeriod.id, program:currentProgram.id]]}">
		<select id="${table}-compare" name="location">
			<option value="0">Please select</option>
			<g:if test="${dashboard != null && dashboard.locationPath != null && !dashboard.locationPath.empty}">
				<g:each in="${dashboard.locationPath}" var="location">
					<option value="${location.id}">
						<g:i18n field="${location.names}" />
					</option>
				</g:each>
			</g:if>
			<g:if test="${table == 'location'}">
				<option value="${currentLocation.id}">
					<g:i18n field="${currentLocation.names}" />
				</option>
			</g:if>
		</select>
	</g:form>
</div>
