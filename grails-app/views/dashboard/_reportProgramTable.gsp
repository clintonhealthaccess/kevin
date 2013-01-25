<table class="nav-table graph">
	<tbody>
		<tr class="parent">
			<g:set var="percentageValue" value="${dashboardTable.getPercentage(currentLocation, dashboardEntity)}" />
			<td>
				<!-- program -->
				<g:i18n field="${currentProgram.names}" />
			</td>
			<td>
				<!-- percentage value -->
				<g:if test="${dashboardEntity.isTarget()}">
					<g:message code="dashboard.report.table.score"/>
				</g:if>
				<g:else>
					<g:if test="${percentageValue != null && !percentageValue.isNull()}">
						<g:reportValue 
							value="${percentageValue}" 
							type="${dashboardTable.type}" 
							format="${dashboardTable.format}"
							tooltip="${reportIndicator.isTarget() ? i18n(field: reportIndicator.names) : i18n(field: currentProgram.names)}"
							/>
					</g:if>
					<g:else>
						<g:message code="report.value.na"/>
					</g:else>
				</g:else>
			</td>					
			<td>
				<!-- percentage value bar -->
				<g:if test="${!reportIndicator.isTarget()}">
				<g:set var="indicator" value="${currentProgram}" />
					<g:if test="${percentageValue != null && !percentageValue.isNull()}">
						<g:if test="${percentageValue.numberValue > 1}">
							<div class="js_bar_horizontal tooltip horizontal-bar-4"
								data-entity="${reportIndicator.id}"
								data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								style="width:100%;"
								original-title="${i18n(field: indicator.names)}: ${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
						</g:if>
						<g:else>
							<g:set var="colorValue" value="${percentageValue.numberValue}"/>
							<g:set var="colorClass" value="${colorValue < 0.26 ? 0 : (colorValue < 0.51 ? 1 : (colorValue < 0.76 ? 2 : (colorValue < 1.01 ? 3 : 4)))}"/>
							<div class="js_bar_horizontal tooltip horizontal-bar-${colorClass}"
								data-entity="${reportIndicator.id}"
								data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								style="width:${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								original-title="${i18n(field: indicator.names)}: ${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
						</g:else>
					</g:if>
					<g:else>
						<div class="js_bar_horizontal tooltip horizontal-bar-na"
							data-entity="${reportIndicator.id}"
							data-percentage="null"
							style="width:0%;"
							original-title="null"></div>
					</g:else>
				</g:if>
			</td>
		</tr>
		<g:set var="dashboardEntities" value="${dashboardEntity.isTarget() ? dashboardEntity : dashboardTable.getIndicators(dashboardEntity)}" />
		<g:each in="${dashboardEntities}" var="entity">
			<tr>
				<g:set var="percentageValue" value="${dashboardTable.getPercentage(currentLocation, entity)}" />
				<td>
					<% def childProgramLinkParams = new HashMap(params) %>
					<% childProgramLinkParams['program'] = entity.program.id+"" %>
					<% childProgramLinkParams['dashboardEntity'] = entity.id+"" %>
					<g:set var="childEntity" value="${entity.isTarget() ? entity : entity.program}" />
					<g:if test="${childEntity == dashboardEntity}">
						<g:i18n field="${childEntity.names}" />
					</g:if>
					<g:else>
						<a href="${createLink(controller: controllerName, action: actionName, params: childProgramLinkParams)}">
							<g:i18n field="${childEntity.names}" />
						</a>
					</g:else>
					&nbsp;
					<g:render template="/templates/help_tooltip" 
						model="[names: i18n(field: childEntity.names), descriptions: i18n(field: childEntity.descriptions)]" />
				</td>
				<td>
					<!-- percentage value -->
					<g:if test="${percentageValue != null && !percentageValue.isNull()}">
						<g:reportValue 
							value="${percentageValue}" 
							type="${dashboardTable.type}" 
							format="${dashboardTable.format}"
							tooltip="${reportIndicator.isTarget() ? i18n(field: reportIndicator.names) : i18n(field: currentProgram.names)}"
							/>
					</g:if>
					<g:else>
						<g:message code="report.value.na"/>
					</g:else>
					</td>					
				<td>
					<!-- percentage value bar -->
					<g:set var="indicator" value="${reportIndicator.isTarget() ? reportIndicator : currentProgram}" />
					<g:if test="${percentageValue != null && !percentageValue.isNull()}">
						<g:if test="${percentageValue.numberValue > 1}">
							<div class="js_bar_horizontal tooltip horizontal-bar-4"
								data-entity="${reportIndicator.id}"
								data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								style="width:100%;"
								original-title="${i18n(field: indicator.names)}: ${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
						</g:if>
						<g:else>
							<g:set var="colorValue" value="${percentageValue.numberValue}"/>
							<g:set var="colorClass" value="${colorValue < 0.26 ? 0 : (colorValue < 0.51 ? 1 : (colorValue < 0.76 ? 2 : (colorValue < 1.01 ? 3 : 4)))}"/>
							<div class="js_bar_horizontal tooltip horizontal-bar-${colorClass}"
								data-entity="${reportIndicator.id}"
								data-percentage="${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								style="width:${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"
								original-title="${i18n(field: indicator.names)}: ${g.reportValue(value: percentageValue, type: reportTable.type, format: reportTable.format)}"></div>
						</g:else>
					</g:if>
					<g:else>
						<div class="js_bar_horizontal tooltip horizontal-bar-na"
							data-entity="${reportIndicator.id}"
							data-percentage="null"
							style="width:0%;"
							original-title="null"></div>
					</g:else>				
					<!-- comparison value -->
					<div id="compare-dashboard-entity-${entity.id}" 
					class="js_bar_horizontal tooltip horizontal-bar-avg hidden" 							
						data-percentage="45" 
						style="width:45%;" 
						original-title="45%"></div>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>