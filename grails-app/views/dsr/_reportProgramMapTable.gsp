<div class='nav-table-wrap'>
	<table class='nav-table number'>
		<tbody>
			<tr class='parent'>
				<td>
					<div class="left"><g:reportLocationParent linkParams="${params}"/></div>
					<g:i18n field="${currentLocation.names}" />					
				</td>
				<g:if test="${reportIndicators != null && !reportIndicators.empty}">
					<g:each in="${reportIndicators}" var="indicator" status="i">
						<td class="${currentIndicators.contains(indicator) ? 'js-selected-indicator selected-indicator' : ''}">
							<div class="js-map-table-indicator" 
								data-indicator-code="${indicator.code}" 
								data-indicator-names="${i18n(field:indicator.names)}"
								data-indicator-class="${'indicator-'+i}">
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
							</div>
						</td>
					</g:each>
				</g:if>
				<g:else>
					<td></td>
				</g:else>
			</tr>
			<g:each in="${reportLocations}" var="location">
				<tr>					
					<td>
						<!-- location -->
						<div class="js-map-table-location" 
							data-location-code="${location.code}" 
							data-location-names="${i18n(field: location.names)}">
							<g:if test="${location.collectsData()}">
								<g:i18n field="${location.names}" />
							</g:if>
							<g:else>
								<%
									locationLinkParams = [:]
									locationLinkParams.putAll linkParams
									locationLinkParams['location'] = location.id+""
								%>
								<a href="${createLink(controller: controllerName, action: actionName,  params: locationLinkParams)}">
								<g:i18n field="${location.names}" /></a>
							</g:else>
						</div>
					</td>
					<g:if test="${reportIndicators != null && !reportIndicators.empty}">
						<g:each in="${reportIndicators}" var="indicator" status="i">
							<g:set var="mapValue" value="${reportTable.getTableReportValue(location, indicator)}"/>
							<g:if test="${mapValue != null || location.collectsData()}">
								<td class="${currentIndicators.contains(indicator) ? 'selected' : ''}">
									<!-- map table report value -->
									<div class="js-map-table-value ${currentIndicators.contains(indicator) ? 'js-selected-value' : ''}"
											data-location-code="${location.code}" 
											data-location-names="${i18n(field: location.names)}" 
											data-indicator-code="${indicator.code}" 
											data-indicator-names="${i18n(field:indicator.names)}"
											data-indicator-class="${'indicator-'+i}">
										<g:reportMapValue 
											value="${indicator.average ? mapValue?.average : mapValue?.value}"
											type="${indicator.getType()}" 
											format="${indicator.getFormat()}"/>
									</div>
								</td>
							</g:if>
							<g:else>
								<td></td>
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
	<p style="font-size:11px; margin-top:10px;">&#185; Denotes FOSA coordinates missing.</p>
	<p style="font-size:11px; margin-top:10px;">&#178; Denotes FOSA facility missing.</p>
</div>