<div class="filter">
	<div class="dropdown white-dropdown">
		<a class="selected" href="#" data-type="question"><g:message code="default.new.label" args="${[entityName]}"/></a>
		<div class="hidden dropdown-list">
			<ul>
				<li><a class="flow-add"
					href="${createLink(controller:'simpleQuestion', action:'create', params:[sectionId: section.id])}">
					<g:message code="default.new.label" args="${[message(code:'survey.simplequestion.label',default:'Simple Question')]}"/>
					</a>
				</li>
				<li><a class="flow-add"
					href="${createLink(controller:'checkboxQuestion', action:'create', params:[sectionId: section.id])}">
					<g:message code="default.new.label" args="${[message(code:'survey.checkboxquestion.label',default:'Checkbox Question')]}"/>
					</a>
				</li>
				<li><a class="flow-add"
					href="${createLink(controller:'tableQuestion', action:'create', params:[sectionId: section.id])}">
					<g:message code="default.new.label" args="${[message(code:'survey.tablequestion.label',default:'Table Question')]}"/>
						</a>
				</li>
			</ul>
		</div>
	</div>
</div>