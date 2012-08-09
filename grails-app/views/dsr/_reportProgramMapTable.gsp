<div class='nav-table-wrap'>
	<table class='nav-table number'>
		<tbody>
			<tr class='parent'>
				<td>
					<g:if test="${currentLocation.parent != null}">
						<%
							parentLocationLinkParams = [:]
							parentLocationLinkParams.putAll linkParams
							parentLocationLinkParams['location'] = currentLocation.parent?.id+""
						%>
						<a class="level-up left" href="${createLink(controller:'dsr', action: 'view', params: parentLocationLinkParams)}">
						<g:message code="report.view.label" args="${[i18n(field: currentLocation.parent.names)]}"/></a>		  
					</g:if>
					<g:i18n field="${currentLocation.names}" />					
				</td>
				<td></td>
				<g:if test="${dsrTable.targets != null && !dsrTable.targets.empty}">
					<g:each in="${dsrTable.targets}" var="target">
						<td>
							<g:if test="${currentTarget == target}"><g:i18n field="${target.names}" /></g:if>
							<g:else>
								<%
									targetLinkParams = [:]
									targetLinkParams.putAll linkParams
									targetLinkParams['dsrTarget'] = target.id+""
								%>
								<a href="${createLink(controller:'dsr', action: 'view', params: targetLinkParams)}">
								<g:i18n field="${target.names}" /></a>
							</g:else>
							<g:render template="/templates/help_tooltip" 
								model="[names: i18n(field: target.names), descriptions: i18n(field: target.descriptions)]" />
						</td>
					</g:each>
				</g:if>
				<g:else>
					<td></td>
				</g:else>
			</tr>
			<g:each in="${dsrTable.locations}" var="location">
				<tr>					
					<td data-location-code="${location.code}">
						<g:if test="${location.collectsData()}"><g:i18n field="${location.names}" /></g:if>
						<g:else>
							<%
								locationLinkParams = [:]
								locationLinkParams.putAll linkParams
								locationLinkParams['location'] = location.id+""
							%>
							<a href="${createLink(controller:'dsr', action: 'view',  params: locationLinkParams)}">
							<g:i18n field="${location.names}" /></a>
						</g:else>
					</td>
					<td></td>
					<g:if test="${dsrTable.targets != null && !dsrTable.targets.empty}">
						<g:each in="${dsrTable.targets}" var="target">
							<g:if test="${viewSkipLevels.contains(currentLocation.level)}"><td></td></g:if>
							<g:else>
								<td>
									<div class="${currentTarget == target ? 'js-map-location' : ''}"
											data-location-names="${i18n(field: location.names)}" 
											data-location-code="${location.code}" data-target-code="${target.code}">
										<g:if test="${dsrTable.getReportValue(location, target) != null}">
											<g:reportValue value="${dsrTable.getReportValue(location, target)}" type="${target.data.type}" format="${target.format}"/>
										</g:if>
										<g:else>
											<div class="report-value report-value-na" data-report-value="${message(code:'report.value.na')}"><g:message code="report.value.na"/></div>
										</g:else>
									</div>																		
								</td>
							</g:else>
						</g:each>
					</g:if>
					<g:else>
						<td></td>
					</g:else>
				</tr>
			</g:each>
		</tbody>
	</table>
	<!-- TODO nav-table.sass & message.properties -->
	<p style="margin-top:10px;">* Denotes location missing map information.</p>
</div>