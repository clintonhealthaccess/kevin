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
							<div class="js-map-table-indicator" 
								data-indicator-code="${indicator.code}" 
								data-indicator-names="${i18n(field:indicator.names)}">
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
							<g:set var="mapValue" value="${reportTable.getPercentage(location, indicator)}" />
							<td>
								<!-- map report value -->
								<div class="js-map-table-value ${mapValue != null && !mapValue.isNull() && mapValue.numberValue > 0 ? 'js-selected-value':''}"
									data-location-code="${location.code}" 
									data-location-names="${i18n(field: location.names)}" 
									data-indicator-code="${indicator.code}" 
									data-indicator-names="${i18n(field:indicator.names)}">
									<div class="report-value">
										<g:reportMapValue
												value="${mapValue}" 
												type="${reportTable.type}" 
												format="${reportTable.format?:'#%'}"
												rounded="0"
											/>
									</div>
								</div>
							</td>
							<td>
								<!-- map report value bar -->
								<g:if test="${mapValue.isNull()}">
									<div class="js_bar_horizontal tooltip horizontal-bar"
										data-entity="${indicator.id}"
										data-percentage="null"
										style="width:0%;"
										original-title="null"></div>
								</g:if>
								<g:elseif test="${mapValue.numberValue <= 1}">
									<div class="js_bar_horizontal tooltip horizontal-bar"
										data-entity="${indicator.id}"
										data-percentage="${g.reportValue(value: mapValue, type: dashbFoard.type, format: reportTable.format)}"
										style="width:${g.reportValue(value: mapValue, type: reportTable.type, format: reportTable.format)}"
										original-title="${g.reportValue(value: mapValue, type: reportTable.type, format: reportTable.format)}"></div>
								</g:elseif>
								<g:else>
									<div class="js_bar_horizontal tooltip horizontal-bar expand-bar"
										data-entity="${indicator.id}"
										data-percentage="${g.reportValue(value: mapValue, type: reportTable.type, format: reportTable.format)}"
										style="width:100%;"
										original-title="${g.reportValue(value: mapValue, type: reportTable.type, format: reportTable.format)}"></div>
								</g:else> 
							</td>
						</g:each>
					</g:if>
					<g:else>
						<td></td>
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