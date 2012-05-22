<div class="filter">
	<div class="js_dropdown dropdown">
		<a class="selected" href="#" data-type="question"><g:message code="default.new.label" args="[entityName]"/></a>
		<div class="hidden js_dropdown-list dropdown-list">
			<ul>
				<li>
					<a href="${createLinkWithTargetURI(controller:'user', action:'create')}">
						<g:message code="default.new.label" args="[message(code:'user.label')]"/>
					</a>
				</li>
				<li>
					<a href="${createLinkWithTargetURI(controller:'dataUser', action:'create')}">
						<g:message code="default.new.label" args="[message(code:'datauser.label')]"/>
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>