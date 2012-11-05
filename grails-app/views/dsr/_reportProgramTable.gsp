<table class="nested push-top-10 ${dsrTable.indicators.size() > 3 ? 'col4' : ''}">
	<thead>
		<tr>
			<th>
				<g:render template="/templates/reportExpandCollapse"/>
				<br/><br/>
				<div class="left" style="margin-left:4px"><g:render template="/templates/reportLocationParent"/></div>
			</th>
			<g:if test="${dsrTable.indicators != null && !dsrTable.indicators.empty}">
				<g:each in="${dsrTable.indicators}" var="target">
					<th>
						<g:i18n field="${target.names}" />
						<g:render template="/templates/help_tooltip" 
							model="[names: i18n(field: target.names), descriptions: i18n(field: target.descriptions)]" />
					</th>
				</g:each>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:render template="/dsr/reportProgramTableTree" model="[location:currentLocation, level:0]"/>				
	</tbody>			
</table>