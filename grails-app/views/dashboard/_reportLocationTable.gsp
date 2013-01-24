<table class="nav-table graph">
	<tbody>
		<tr class='parent'>
			<g:set var="percentageValue" value="${reportTable.getPercentage(currentLocation, reportIndicator)}" />
			<td>
				<!-- location -->
				<g:i18n field="${currentLocation.names}" />
			</td>
			<td>
				<!-- percentage value -->
				<div class="js-map-table-value ${percentageValue != null && !percentageValue.isNull() && percentageValue.numberValue > 0 ? 'js-selected-value':''}"
					data-location-code="${currentLocation.code}" 
					data-location-names="${i18n(field: currentLocation.names)}" 
					data-indicator-code="${reportIndicator.code}" 
					data-indicator-names="${i18n(field:reportIndicator.names)}">
					<g:reportMapValue
						value="${percentageValue}" 
						type="${reportTable.type}" 
						format="${reportTable.format?:'#%'}"
						rounded="0"
						tooltip="${reportIndicator.isTarget() ? i18n(field:reportIndicator.names) : i18n(field:currentProgram.names)}"
					/>
				</div>
			</td>
			<td>
				<!-- percentage value bar -->
				<g:if test="${percentageValue != null && !percentageValue.isNull()}">
					<g:set var="indicator" value="${reportIndicator.isTarget() ? reportIndicator : currentProgram}" />
					<g:if test="${percentageValue.numberValue > 1}">
						<div class="js_bar_horizontal tooltip horizontal-bar-4"
							data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
							style="width:100%;"
							original-title="${i18n(field: indicator.names)}: ${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
					</g:if>
					<g:else>
						<g:set var="colorValue" value="${percentageValue.numberValue}"/>
						<g:set var="colorClass" value="${colorValue < 0.26 ? 0 : (colorValue < 0.51 ? 1 : (colorValue < 0.76 ? 2 : (colorValue < 1.01 ? 3 : 4)))}"/>
						<div class="js_bar_horizontal tooltip horizontal-bar-${colorClass}"
							data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
							style="width:${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
							original-title="${i18n(field: indicator.names)}: ${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
					</g:else>
				</g:if>
				<g:else>
					<div class="js_bar_horizontal tooltip horizontal-bar-na"
						data-percentage="null"
						style="width:0%;"
						original-title="${i18n(field:reportIndicator.names)}"></div>
				</g:else> 
			</td>
		</tr>
		<g:each in="${reportLocations}" var="location">
			<tr>
				<g:set var="percentageValue" value="${reportTable.getPercentage(location, reportIndicator)}" />
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
				<td>
					<!-- percentage value -->
					<div class="js-map-table-value ${percentageValue != null && !percentageValue.isNull() && percentageValue.numberValue > 0 ? 'js-selected-value':''}"
						data-location-code="${location.code}" 
						data-location-names="${i18n(field: location.names)}" 
						data-indicator-code="${reportIndicator.code}" 
						data-indicator-names="${i18n(field:reportIndicator.names)}">
						<div class="report-value">
							<g:reportMapValue
									value="${percentageValue}" 
									type="${reportTable.type}" 
									format="${reportTable.format?:'#%'}"
									rounded="0"
									tooltip="${reportIndicator.isTarget() ? i18n(field:reportIndicator.names) : i18n(field:currentProgram.names)}"
								/>
						</div>
					</div>
				</td>
				<td>
					<!-- percentage value bar -->
					<g:if test="${percentageValue != null && !percentageValue.isNull()}">
						<g:set var="indicator" value="${reportIndicator.isTarget() ? reportIndicator : currentProgram}" />
						<g:if test="${percentageValue.numberValue > 1}">
							<div class="js_bar_horizontal tooltip horizontal-bar-4"
								data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								style="width:100%;"
								original-title="${i18n(field: indicator.names)}: ${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
						</g:if>
						<g:else>
							<g:set var="colorValue" value="${percentageValue.numberValue}"/>
							<g:set var="colorClass" value="${colorValue < 0.26 ? 0 : (colorValue < 0.51 ? 1 : (colorValue < 0.76 ? 2 : (colorValue < 1.01 ? 3 : 4)))}"/>
							<div class="js_bar_horizontal tooltip horizontal-bar-${colorClass}"
								data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								style="width:${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								original-title="${i18n(field: indicator.names)}: ${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
						</g:else>
					</g:if>
					<g:else>
						<div class="js_bar_horizontal tooltip horizontal-bar-na"
							data-percentage="null"
							style="width:0%;"
							original-title="${i18n(field:reportIndicator.names)}"></div>
					</g:else>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<!-- comparison value -->
<div class="horizontal-graph-avg hidden" data-entity="${dashboardEntity.id}">
	<div class="horizontal-graph-tip tooltip" style="left: 63%;" title="63%" data-percentage="63">?</div>
	<div class="horizontal-graph-marker"></div>
</div>