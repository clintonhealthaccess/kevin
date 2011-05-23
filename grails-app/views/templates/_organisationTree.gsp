<li>
	<% params['organisation'] = organisation.id %>
	<g:if test="${organisation.level < displayLinkUntil}">
		${organisation.name}
	</g:if>
	<g:else>
		<a class="parameter" data-type="organisation" data-organisation="${organisation.id}" href="${createLink(controller:controller, action:action, params:params)}">
			${organisation.name}
		</a>
	</g:else>
	<g:if test="${organisation.children != null}">
		<ul>
			<g:each in="${organisation.children}" var="child">
				<g:render template="/templates/organisationTree" model="[ controller: controller, action: action, organisation: child, params:params, displayLinkUntil: displayLinkUntil]"/>
			</g:each>
		</ul>
	</g:if>
</li>