<div class="filter">
	<div class="js_dropdown dropdown">
		<a class="selected" href="#" data-type="question"><g:message code="default.new.label" args="[entityName]"/></a>
		<div class="hidden dropdown-list js_dropdown-list">
			<ul>
				<li>
					<a href="${createLinkWithTargetURI(controller:'simpleQuestion', action:'create', params:['section.id': section.id])}">
						<g:message code="default.new.label" args="[message(code:'survey.simplequestion.label')]"/>
					</a>
				</li>
				<li>
					<a href="${createLinkWithTargetURI(controller:'checkboxQuestion', action:'create', params:['section.id': section.id])}">
						<g:message code="default.new.label" args="[message(code:'survey.checkboxquestion.label')]"/>
					</a>
				</li>
				<li>
					<a href="${createLinkWithTargetURI(controller:'tableQuestion', action:'create', params:['section.id': section.id])}">
						<g:message code="default.new.label" args="[message(code:'survey.tablequestion.label')]"/>
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>