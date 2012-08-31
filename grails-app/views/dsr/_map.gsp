<li>
	<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>
	<g:reportView linkParams="${params}" exclude="${['indicators']}"/>
	
	<div class="selector">
		<g:reportCategoryFilter linkParams="${params}" exclude="${['indicators']}"/>
	</div>
	
	<g:render template="/dsr/reportProgramMap" model="[linkParams:params]"/>
	<g:render template="/dsr/reportProgramMapTable" 
		model="[linkParams:params, reportTable: dsrTable, reportLocations: dsrTable.locations, reportIndicators: dsrTable.targets]"/>
</li