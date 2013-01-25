<li>
	<g:render template="/templates/reportTitle" model="[entity: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']" />
	<g:reportExport linkParams="${params}" />
	<g:reportView linkParams="${params}"/>
	
	<div class="selector">
		<g:reportIndicatorFilter selected="${currentTarget}" 
			selectedIndicatorClass="${selectedTargetClass}" selectedIndicatorParam="fctTarget" 
			program="${currentProgram}" linkParams="${params}"/>
	</div>
	
	<!-- legend -->
	<g:render template="/fct/legend" model="[indicators: fctTable.indicators]"/>

	<g:render template="/fct/reportProgramMap" 
		model="[linkParams:params, reportTable: fctTable, reportLocations: fctTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes), reportIndicators: fctTable.indicators]"/>
	<g:render template="/fct/reportProgramMapTable" 
		model="[linkParams:params, reportTable: fctTable, reportLocations: fctTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes), reportIndicators: fctTable.indicators]"/>
</li>