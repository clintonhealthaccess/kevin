<li>
	<g:reportExport linkParams="${params}" />
	<g:set var="reportTitleEntity" value="${dashboardEntity.isTarget() ? dashboardEntity : currentProgram}" />
	<g:render template="/templates/reportTitle" model="[entity: reportTitleEntity, title: i18n(field: reportTitleEntity.names), descriptions: i18n(field: reportTitleEntity.names), file: 'star_small.png']"/>
	<g:reportProgramParent linkParams="${params}" exclude="${['dashboardEntity']}" />
	<g:reportView linkParams="${params}" />

	<div class="selector">
		<g:reportIndicatorFilter selected="${dashboardEntity.isTarget() ? dashboardEntity : null}" 
			selectedIndicatorClass="${selectedTargetClass}" selectedIndicatorParam="dashboardEntity" 
			program="${currentProgram}" linkParams="${params}"/>
	</div>

	<!-- legend -->
	<g:render template="/dashboard/legend" />

	<g:render template="/dashboard/reportProgramMap" 
	model="[linkParams:params, reportTable: dashboardTable, reportLocations: dashboardTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes), reportIndicators: dashboardTable.getIndicators(dashboardEntity), reportIndicator: dashboardEntity]"/>
	<g:render template="/dashboard/reportProgramMapTable" 
		model="[linkParams:params, reportTable: dashboardTable, reportLocations: dashboardTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes), reportIndicators: dashboardTable.getIndicators(dashboardEntity), reportIndicator: dashboardEntity]"/>
</li>