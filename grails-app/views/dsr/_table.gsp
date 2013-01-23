<li>
	<g:render template="/templates/reportTitle" model="[entity: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>
	<g:reportExport linkParams="${params}" />
	<g:reportView linkParams="${params}" exclude="${['indicators']}"/>
	<g:reportCategoryFilter selected="${currentCategory}" program="${currentProgram}" linkParams="${params}" exclude="${['indicators']}"/>
	
	<g:render template="/dsr/reportProgramTable"/>
</li>