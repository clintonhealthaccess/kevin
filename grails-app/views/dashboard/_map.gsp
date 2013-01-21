<li>
	<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>
	<g:reportExport linkParams="${params}" />
	<g:reportView linkParams="${params}" />
	
	<!-- TODO legend & colors -->
	<g:render template="/maps/legend" model="[indicators: fctTable.indicators]"/>
	<g:render template="/maps/colors"/>

	<g:render template="/dashboard/reportProgramMap" 
	model="[linkParams:params, reportTable: dashboard, reportLocations: dashboard.getLocations(currentLocation, locationSkipLevels, currentLocationTypes), reportIndicators: dashboard.getIndicators()]"/>
	<g:render template="/dashboard/reportProgramMapTable" 
		model="[linkParams:params, reportTable: dashboard, reportLocations: dashboard.getLocations(currentLocation, locationSkipLevels, currentLocationTypes), reportIndicators: dashboard.getIndicators()]"/>
</li>