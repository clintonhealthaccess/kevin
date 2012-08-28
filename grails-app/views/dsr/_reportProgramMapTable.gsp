<div class='nav-table-wrap'>
	<table class='nav-table number'>
		<tbody>
			<tr class='parent'>
				<td>
					<div class="left"><g:render template="/templates/reportLocationParent"/></div>
					<g:i18n field="${currentLocation.names}" />					
				</td>
				<g:if test="${reportIndicators != null && !reportIndicators.empty}">
					<g:each in="${reportIndicators}" var="indicator">
						<td class="${currentIndicators.contains(indicator) ? 'selected' : ''}">
							<g:if test="${currentIndicators.contains(indicator)}">
								<g:i18n field="${indicator.names}" />
							</g:if>
							<g:else>
								<%
									indicatorLinkParams = [:]
									indicatorLinkParams.putAll linkParams
									indicatorLinkParams['indicators'] = indicator.id+""
								%>
								<a href="${createLink(controller: controllerName, action: actionName, params: indicatorLinkParams)}">
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
							<a href="${createLink(controller: controllerName, action: actionName,  params: locationLinkParams)}">
							<g:i18n field="${location.names}" /></a>
						</g:else>
					</td>
					<g:if test="${reportIndicators != null && !reportIndicators.empty}">
						<g:each in="${reportIndicators}" var="indicator">
							<g:if test="${viewSkipLevels != null && viewSkipLevels.contains(currentLocation.level)}"><td></td></g:if>
							<g:else>
								<td class="${currentIndicators.contains(indicator) ? 'selected' : ''}">
									<div class="js-map-table-value ${currentIndicators.contains(indicator) ? 'js-selected-value' : ''}"
											data-location-code="${location.code}" 
											data-location-names="${i18n(field: location.names)}"
											data-indicator-code="${indicator.code}" 
											data-indicator-names="${i18n(field:indicator.names)}">
										<g:reportMapValue value="${reportTable.getMapReportValue(location, indicator)}" 
															type="${indicator.getType()}" 
															format="${indicator.getFormat()}"/>
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
	<p style="font-size:11px; margin-top:10px;">&#185; Denotes missing FOSA coordinates.</p>
	<p style="font-size:11px; margin-top:10px;">&#178; Denotes missing FOSA facility.</p>
</div>