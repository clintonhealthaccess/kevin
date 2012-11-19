<div class="selector right">
	<span><g:message code="dashboard.report.compare.selector"/></span>
	<g:form name="${table}-form" method="get"
		url="${[controller:'dashboard', action:'compare',
			params:[table:table, period:currentPeriod.id, program:currentProgram.id]]}">
		<select id="${table}-compare" name="location" onchange="dashboardFilterChange(this, '${table}'); return false;">
			<option value="0">Please select</option>
			<g:if test="${locationPath != null && !locationPath.empty}">
				<g:each in="${locationPath}" var="location">
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
