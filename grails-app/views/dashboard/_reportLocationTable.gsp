<table class="nav-table graph">
	<tbody>
		<tr class='parent'>
			<g:set var="percentageValue" value="${reportTable.getPercentage(currentLocation, reportIndicator)}" />
			<td>
				<!-- location -->
				<g:i18n field="${currentLocation.names}" />
			</td>
			<td>
				<!-- map report value -->
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
					/>
				</div>
			</td>
			<td>
				<!-- map report value bar -->
				<g:if test="${percentageValue != null && !percentageValue.isNull()}">
					<g:if test="${percentageValue.numberValue > 1}">
						<div class="js_bar_horizontal tooltip horizontal-bar expand-bar"
							data-entity="${reportIndicator.id}"
							data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
							style="width:100%;"
							original-title="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
					</g:if>
					<g:else>
						<div class="js_bar_horizontal tooltip horizontal-bar"
							data-entity="${reportIndicator.id}"
							data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
							style="width:${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
							original-title="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
					</g:else>
				</g:if>
				<g:else>
					<div class="js_bar_horizontal tooltip horizontal-bar"
						data-entity="${reportIndicator.id}"
						data-percentage="null"
						style="width:0%;"
						original-title="null"></div>
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
					<!-- map report value -->
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
								/>
						</div>
					</div>
				</td>
				<td>
					<!-- map report value bar -->
					<g:if test="${percentageValue != null && !percentageValue.isNull()}">
						<g:if test="${percentageValue.numberValue > 1}">
							<div class="js_bar_horizontal tooltip horizontal-bar expand-bar"
								data-entity="${reportIndicator.id}"
								data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								style="width:100%;"
								original-title="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
						</g:if>
						<g:else>
							<div class="js_bar_horizontal tooltip horizontal-bar"
								data-entity="${reportIndicator.id}"
								data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								style="width:${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								original-title="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
						</g:else>
					</g:if>
					<g:else>
						<div class="js_bar_horizontal tooltip horizontal-bar"
							data-entity="${reportIndicator.id}"
							data-percentage="null"
							style="width:0%;"
							original-title="null"></div>
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