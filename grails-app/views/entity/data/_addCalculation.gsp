<div class="filter">
	<div class="js_dropdown dropdown">
		<a class="selected" href="#" data-type="question"><g:message code="default.new.label" args="[entityName]"/></a>
		<div class="hidden js_dropdown-list dropdown-list">
			<ul>
				<li>
					<a href="${createLinkWithTargetURI(controller:'average', action:'create')}">
						<g:message code="default.new.label" args="[message(code:'average.label')]"/>
					</a>
				</li>
				<li>
					<a href="${createLinkWithTargetURI(controller:'sum', action:'create')}">
						<g:message code="default.new.label" args="[message(code:'sum.label')]"/>
					</a>
				</li>
				<li>
					<a href="${createLinkWithTargetURI(controller:'aggregation', action:'create')}">
						<g:message code="default.new.label" args="[message(code:'aggregation.label')]"/>
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>