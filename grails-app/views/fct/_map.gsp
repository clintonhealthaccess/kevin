<li>
	<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']" />
	<g:reportView linkParams="${params}"/>
	<div class="selector">
		<g:reportTargetFilter linkParams="${params}" />
		<g:reportValueFilter linkParams="${params}"/>
	</div>
	
	<div class="indicators-selected">
		<!-- chart legend -->
		<ul class="horizontal chart_legend">
			<g:if test="${fctTable != null && fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
				<g:each in="${fctTable.targetOptions}" var="targetOption" status="i">
					<li>
					<span class="${i == fctTable.targetOptions.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}"></span>
					<g:i18n field="${targetOption.names}" /></li>
				</g:each>
			</g:if>
		</ul>
	</div>
	
	<g:render template="/fct/reportProgramMap" model="[linkParams:params]"/>
	<g:render template="/fct/reportProgramMapTable" 
		model="[linkParams:params, reportTable: fctTable, reportLocations: fctTable.locations, reportIndicators: fctTable.targetOptions]"/>
</li>