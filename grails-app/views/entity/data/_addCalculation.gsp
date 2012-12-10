<span class="js_dropdown dropdown">
	<a class="js_dropdown-link with-highlight" href="#"><g:message code="default.new.label" args="[entityName]"/></a>
	<div class="js_dropdown-list dropdown-list">
		<ul>
			<li>
				<a href="${createLinkWithTargetURI(controller:'summ', action:'create')}">
					<g:message code="default.new.label" args="[message(code:'summ.label')]"/>
				</a>
			</li>
			<li>
				<a href="${createLinkWithTargetURI(controller:'mode', action:'create')}">
					<g:message code="default.new.label" args="[message(code:'mode.label')]"/>
				</a>
			</li>
			<li>
				<a href="${createLinkWithTargetURI(controller:'aggregation', action:'create')}">
					<g:message code="default.new.label" args="[message(code:'aggregation.label')]"/>
				</a>
			</li>
		</ul>
	</div>
</span>
