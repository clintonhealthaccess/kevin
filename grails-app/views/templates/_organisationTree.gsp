<li class="${current?.id == organisation.id?'current':''} foldable">
	<% params['organisation'] = organisation.id %>
	<g:if test="${organisation.children != null}">
		<a class="foldable-toggle" href="#">(toggle)</a>
	</g:if>
	<g:if test="${organisation.level < displayLinkUntil}">
		<span>${organisation.name}</span>
	</g:if>
	<g:else>
		<a class="dropdown-link" data-type="organisation" data-organisation="${organisation.id}" href="${createLink(controller:controller, action:action, params:params)}">
			${organisation.name}
		</a>
	</g:else>
	<g:if test="${organisation.children != null}">
		<ul id="organisation-fold-${organisation.id}">
			<g:each in="${organisation.children}" var="child">
				<g:render template="/templates/organisationTree" model="[ controller: controller, action: action, organisation: child, current: current, params:params,linkLevel: linkLevel, displayLinkUntil: displayLinkUntil]"/>
			</g:each>
		</ul>
	</g:if>
</li>