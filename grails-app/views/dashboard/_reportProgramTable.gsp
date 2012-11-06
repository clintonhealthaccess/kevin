<table class="horizontal-graph">
<thead>
  <tr>
	<th>
		<g:i18n field="${currentProgram.names}"/>
		&nbsp;
		<g:render template="/templates/help_tooltip" 
			model="[names: i18n(field: currentProgram.names), descriptions: i18n(field: currentProgram.descriptions)]" />
	</th>	
	<th><g:message code="dashboard.report.table.score"/></th>
	<th></th>
  </tr>
</thead>
	<tbody>
		<g:each in="${dashboard.getIndicators(dashboardEntity)}" var="entity">			
			<tr>
				<g:set var="percentageValue" />
				<td>
					<g:if test="${!entity.isTarget()}">
					 	<% def childProgramLinkParams = new HashMap(params) %>
						<% childProgramLinkParams['program'] = entity.program.id+"" %>
						<% childProgramLinkParams['dashboardEntity'] = entity.id+"" %>										
						<a href="${createLink(controller: controllerName, action: actionName, params: childProgramLinkParams)}">
							<g:i18n field="${entity.program.names}" />
						</a>
						&nbsp;
						<g:render template="/templates/help_tooltip" 
							model="[names: i18n(field: entity.program.names), descriptions: i18n(field: entity.program.descriptions)]" />
					</g:if> 
					<g:else>
						<g:i18n field="${entity.names}" />
						&nbsp;
						<g:render template="/templates/help_tooltip" 
							model="[names: i18n(field: entity.names), descriptions: i18n(field: entity.descriptions)]" />
					</g:else>									 
					</td>
				<td>
					<g:set var="percentageValue" value="${dashboard.getPercentage(currentLocation, entity)}" />
					<g:if test="${!percentageValue.isNull()}">
						<g:reportValue value="${percentageValue}" type="${dashboard.type}" format="${dashboard.format}"/>
					</g:if>
					<g:else>
						<g:message code="report.value.na"/>
					</g:else>
					</td>					
				<td>
					<!-- percentage value -->
					<g:if test="${percentageValue.isNull()}">
						<div class="js_bar_horizontal tooltip horizontal-bar" 
							data-percentage="null"
							style="width:0%"							 
							original-title="null"></div>
					</g:if>
					<g:elseif test="${percentageValue.numberValue <= 1}">
						<div class="js_bar_horizontal tooltip horizontal-bar" 
							data-percentage="${g.reportValue(value: percentageValue, type: dashboard.type, format: dashboard.format)}"
							style="width:${g.reportValue(value: percentageValue, type: dashboard.type, format: dashboard.format)}"							 
							original-title="${g.reportValue(value: percentageValue, type: dashboard.type, format: dashboard.format)}"></div>
					</g:elseif>
					<g:else>
						<div class="js_bar_horizontal tooltip horizontal-bar expand-bar" 
							data-percentage="${g.reportValue(value: percentageValue, type: dashboard.type, format: dashboard.format)}"
							style="width:100%"
							original-title="${g.reportValue(value: percentageValue, type: dashboard.type, format: dashboard.format)}"></div>
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