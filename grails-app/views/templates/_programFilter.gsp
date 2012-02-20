<div class="filter">
	<span class="dropdown js_dropdown">
		<g:if test="${currentObjective != null}">
			<a href="#" class="program selected"
				data-objective="${currentObjective.id}" data-type="objective"> <g:i18n
					field="${currentObjective.names}" />
			</a>
		</g:if> <g:else>
			<a href="#" class="program selected" data-type="objective"> <g:message
					code="filter.program.noselection.label"
					default="Please select a program" />
			</a>
		</g:else>
		<div class="hidden dropdown-list js_dropdown-list">
			<ul>
				<g:render template="/templates/programTree"
					model="[
					controller: controllerName, 
					action: actionName,
					current: currentObjective,
					objective: objectiveRoot,
					linkParams: linkParams]" />
			</ul>
		</div>
	</span>
</div>