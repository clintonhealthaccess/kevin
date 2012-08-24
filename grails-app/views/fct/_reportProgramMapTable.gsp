<div class='nav-table-wrap'>
	<table class='nav-table number'>
		<tbody>
			<tr class='parent'>
				<td>
					<div class="left"><g:render template="/templates/reportLocationParent"/></div>
					<g:i18n field="${currentLocation.names}" />
				</td>
				<g:if test="${reportIndicators != null && !reportIndicators.empty}">
					<g:each in="${reportIndicators}" var="indicator" status="i">
						<td>
							<g:i18n field="${indicator.names}" />
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
						<g:each in="${reportIndicators}" var="indicator" status="i">
							<g:if test="${viewSkipLevels != null && viewSkipLevels.contains(currentLocation.level)}"><td></td></g:if>
							<g:else>
								<td>
									<g:set var="mapReportValue" value="${reportTable.getMapReportValue(location, indicator)}"/>
									<div class="js-map-table-value ${mapReportValue != null && !mapReportValue.isNull() && mapReportValue.numberValue > 0 ? 'js-selected-value':''}"
											data-location-code="${location.code}" 
											data-location-names="${i18n(field: location.names)}" 
											data-indicator-code="${indicator.code}" 
											data-indicator-names="${i18n(field:indicator.names)}"
											data-indicator-class="${i == reportIndicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}">
										<div class="report-value-number">
											<g:mapReportValue value="${reportTable.getMapReportValue(location, indicator)}"
															type="${indicator.type}"
															format="${indicator.numberFormat}"/>
										</div>
										<g:if test="${!location.collectsData()}">
											<div class="report-value-percentage hidden">
												<g:reportPercentage value="${reportTable.getMapReportPercentage(location, indicator)}" 
																type="${indicator.type}" 
																format="${indicator.percentageFormat}"/>
											</div>
										</g:if>
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