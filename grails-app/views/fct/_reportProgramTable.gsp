<table class="nested push-top-10 push-10 ${fctTable.indicators.size() > 3 ? 'col4' : ''}">
	<thead>
		<tr>
			<th>
				<g:render template="/templates/reportExpandCollapse"/>
				<br/><br/>
				<div class="left" style="margin-left:4px"><g:render template="/templates/reportLocationParent"/></div>						
			</th>
			<g:if test="${fctTable.indicators != null && !fctTable.indicators.empty}">
				<g:each in="${fctTable.indicators}" var="targetOption">
					<th>						
						<g:i18n field="${targetOption.names}" />
						<g:render template="/templates/help_tooltip" 
							model="[names: i18n(field: targetOption.names), descriptions: i18n(field: targetOption.descriptions)]" />
					</th>
				</g:each>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:render template="/fct/reportProgramTableTree" model="[location:currentLocation, level:0]"/>				
	</tbody>			
</table>