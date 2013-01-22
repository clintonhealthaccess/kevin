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
						<td>
							<div class="js-map-table-indicator" 
								data-indicator-code="${indicator.code}" 
								data-indicator-names="${i18n(field:indicator.names)}"
								data-indicator-class="${i == reportIndicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}">
								<g:i18n field="${indicator.names}" />
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
							<td>
								<!-- map table report value -->
								<g:set var="mapValue" value="${reportTable.getTableReportValue(location, indicator)?.value}"/>
								<div class="js-map-table-value ${mapValue != null && !mapValue.isNull() && mapValue.numberValue > 0 ? 'js-selected-value':''}"
									data-location-code="${location.code}" 
									data-location-names="${i18n(field: location.names)}" 
									data-indicator-code="${indicator.code}" 
									data-indicator-names="${i18n(field:indicator.names)}"
									data-indicator-class="${i == reportIndicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}">
									<div class="report-value-number">
										<g:reportMapValue 
											value="${mapValue}"
											type="${indicator.type}"
											format="${indicator.numberFormat}"/>
									</div>
									<g:if test="${!location.collectsData()}">
										<div class="report-value-percentage hidden">
											<g:reportMapValue
												value="${reportTable.getTableReportValue(location, indicator)?.average}" 
												type="${indicator.type}" 
												format="${indicator.percentageFormat?:'#%'}"
												rounded="2"
											/>
										</div>
									</g:if>
								</div>
							</td>
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