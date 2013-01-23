<g:reportExport linkParams="${params}" />
<ul class="clearfix">
	<li class="push-20">
		<g:set var="reportTitleEntity" value="${dashboardEntity.isTarget() ? dashboardEntity : currentProgram}" />
		<g:render template="/templates/reportTitle" model="[entity: reportTitleEntity, title: i18n(field: reportTitleEntity.names), descriptions: i18n(field: reportTitleEntity.names), file: 'star_small.png']"/>								
		<g:reportProgramParent linkParams="${params}" exclude="${['dashboardEntity']}" />
		<g:reportView linkParams="${params}" />
		<div class="selector">
				<g:reportIndicatorFilter selected="${dashboardEntity.isTarget() ? dashboardEntity : null}" 
					selectedIndicatorClass="${selectedTargetClass}" selectedIndicatorParam="dashboardEntity" 
					program="${currentProgram}" linkParams="${params}"/>
			</div>
		<g:render template="/dashboard/reportCompareFilter" model="[table:'program', locationPath: dashboardTable.locationPath - currentLocation]"/>
		<!-- program table -->
		<div class="horizontal-graph-wrap">
			<g:render template="/dashboard/reportProgramTable" />
		</div>
	</li>
	<li class="push-10">
		<g:render template="/templates/reportTitle" model="[title: i18n(field: currentLocation.names), file: 'marker_small.png']"/>
		<g:reportLocationParent linkParams="${params}" />				
		<g:render template="/dashboard/reportCompareFilter" model="[table:'location', locationPath: dashboardTable.locationPath]"/>
		<!-- location table -->
		<div class="horizontal-graph-wrap">
			<g:render template="/dashboard/reportLocationTable" model="[linkParams:params, reportTable: dashboardTable, reportLocations: dashboardTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes), reportIndicator: dashboardEntity]"/>
		</div>
	</li>
</ul>