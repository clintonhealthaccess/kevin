<div class="filter">
	
	<span class="dropdown js_dropdown">
		<g:if test="${currentProgram != null}">
			<a href="#" class="program selected" data-program="${currentProgram.id}" data-type="program"> 
				<g:i18n field="${currentProgram.names}" />
			</a>
		</g:if> 
		<g:else>
			<a href="#" class="program selected" data-type="program"> 
				<g:message code="filter.program.noselection.label" />
			</a>
		</g:else>
		<div class="hidden dropdown-list js_dropdown-list">
			<g:if test="${programTree != null && !programTree.empty}">
				<ul>	
					<g:render template="/tags/filter/programTree"
						model="[				
						controller: controllerName, 
						action: actionName,
						current: currentProgram,
						program: programRoot,
						programTree: programTree,
						linkParams:linkParams]" />
				</ul>
			</g:if>
		</div>		
	</span>
</div>