<li>
	<g:render template="/templates/reportTitle" model="[entity: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>
	<g:reportExport linkParams="${params}" />
	<g:reportView linkParams="${params}"/>                            																
	<div class="selector">
		<g:reportIndicatorFilter selected="${currentTarget}" 
			selectedIndicatorClass="${selectedTargetClass}" selectedIndicatorParam="fctTarget" 
			program="${currentProgram}" linkParams="${params}"/>
		<g:reportValueFilter linkParams="${params}"/>
	</div>
	<g:render template="/fct/reportProgramTable" model="[linkParams:params]"/>
</li>
<li>
	<g:render template="/templates/reportTitle" model="[title: i18n(field:currentLocation.names), file: 'marker_small.png']"/>
	<g:reportLocationParent linkParams="${params}"/>
	<div>
		<div>
			<g:message code="fct.report.datalocationtype"/>:
			<g:each in="${currentLocationTypes}" var="dataLocationType" status="i">						
				<g:i18n field="${dataLocationType.names}" /><g:if test="${i != currentLocationTypes.size()-1}">, </g:if>
			</g:each>
		</div>
		<g:render template="/fct/reportLocationBarChart" model="[linkParams:params]"/>
	</div>
</li>