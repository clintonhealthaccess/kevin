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
						<a class="level-up left" href="${createLink(controller: controllerName, action: 'view', params: parentLocationLinkParams)}">
						<g:message code="report.view.label" args="${[i18n(field: currentLocation.parent.names)]}"/></a>		  
					</g:if>
					<g:i18n field="${currentLocation.names}" />					
				</td>
				<g:if test="${reportIndicators != null && !reportIndicators.empty}">
					<g:each in="${reportIndicators}" var="indicator" status="i">
						<td class="${i == reportIndicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}">
							<g:if test="${currentIndicators.contains(indicator)}"><g:i18n field="${indicator.names}" /></g:if>
							<g:else>
								<%
									indicatorLinkParams = [:]
									indicatorLinkParams.putAll linkParams
									indicatorLinkParams['indicators'] = indicator.id+""
								%>
								<a href="${createLink(controller: controllerName, action: 'view', params: indicatorLinkParams)}">
								<g:i18n field="${indicator.names}" /></a>
							</g:else>
							<g:render template="/templates/help_tooltip" 
								model="[names: i18n(field: indicator.names), descriptions: i18n(field: indicator.descriptions)]" />
						</td>
					</g:each>
				</g:if>
				<g:else>
					<td></td>
				</g:else>
			</tr>
			<g:each in="${reportLocations}" var="location">
				<tr>					
					<td data-location-code="${location.code}">
						<g:if test="${location.collectsData()}"><g:i18n field="${location.names}" /></g:if>
						<g:else>
							<%
								locationLinkParams = [:]
								locationLinkParams.putAll linkParams
								locationLinkParams['location'] = location.id+""
							%>
							<a href="${createLink(controller: controllerName, action: 'view',  params: locationLinkParams)}">
							<g:i18n field="${location.names}" /></a>
						</g:else>
					</td>
					<g:if test="${reportIndicators != null && !reportIndicators.empty}">
						<g:each in="${reportIndicators}" var="indicator" status="i">
							<g:if test="${viewSkipLevels != null && viewSkipLevels.contains(currentLocation.level)}"><td></td></g:if>
							<g:else>
								<td>
									<g:set var="reportValue" value="${reportTable.getReportValue(location, indicator)}"/>
									<g:if test="${reportValue != null && reportValue.numberValue > 0}">
										<div class="js-map-location" data-location-code="${location.code}" data-location-names="${i18n(field: location.names)}" 
											data-indicator-code="${indicator.code}" data-indicator-names="${i18n(field:indicator.names)}"
											data-indicator-class="${i == reportIndicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}">
											<g:mapReportValue value="${reportTable.getMapReportValue(location, indicator)}" type="${indicator.getType()}" format="${indicator.getFormat()}"/>
										</div>
									</g:if>
									<g:elseif test="${reportValue != null && reportValue.numberValue == 0}">
										<div>
											<g:mapReportValue value="${reportTable.getMapReportValue(location, indicator)}" type="${indicator.getType()}" format="${indicator.getFormat()}"/>
										</div>
									</g:elseif>
									<g:else>
										<div class="report-value report-value-na" data-report-value="${message(code:'report.value.na')}"><g:message code="report.value.na"/></div>
									</g:else>
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
	<p style="margin-top:10px">* Denotes location missing map information.</p>
</div>