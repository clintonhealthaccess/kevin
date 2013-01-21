<ul class="clearfix">
	<li class="push-20">
		<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field: currentProgram.names), descriptions: i18n(field: currentProgram.names), file: 'star_small.png']"/>														
		<g:render template="/templates/reportProgramParent"/>

		<g:if test="${dashboard.hasData()}">
			<g:render template="/dashboard/reportCompareFilter" model="[table:'program', locationPath: dashboard.locationPath - currentLocation]"/>
			<div class="horizontal-graph-wrap">
				<g:render template="/dashboard/reportProgramTable" model="[dashboard:dashboard]"/>
			</div>
		</g:if>
		<g:else>
			<div class="horizontal-graph-wrap">
				<g:message code="dashboard.report.table.noselection.label"/>
			</div>
		</g:else>
	</li>
	<li class="push-10">
		<g:render template="/templates/reportTitle" model="[title: i18n(field: currentLocation.names), file: 'marker_small.png']"/>
		<g:render template="/templates/reportLocationParent"/>
						
		<g:if test="${dashboard.hasData()}">
			<g:render template="/dashboard/reportCompareFilter" model="[table:'location', locationPath: dashboard.locationPath]"/>
			<div class="horizontal-graph-wrap">
				<g:render template="/dashboard/reportLocationTable" model="[dashboard:dashboard]"/>							  
			</div>
		</g:if>
		<g:else>
			<div class="horizontal-graph-wrap">
				<g:message code="dashboard.report.table.noselection.label"/>
			</div>
		</g:else>
	</li>
</ul>