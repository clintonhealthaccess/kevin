<li>
	<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']" />
	<g:reportExport linkParams="${params}" />
	<g:reportView linkParams="${params}"/>
	<div class="selector">
		<g:reportTargetFilter linkParams="${params}" />
	</div>
	
	<g:render template="/maps/legend" model="[indicators: fctTable.targetOptions]"/>
	<g:render template="/maps/colors"/>
	<g:render template="/fct/reportProgramMap" 
		model="[linkParams:params, reportTable: fctTable, reportLocations: fctTable.locations, reportIndicators: fctTable.targetOptions]"/>
	<g:render template="/fct/reportProgramMapTable" 
		model="[linkParams:params, reportTable: fctTable, reportLocations: fctTable.locations, reportIndicators: fctTable.targetOptions]"/>
</li>