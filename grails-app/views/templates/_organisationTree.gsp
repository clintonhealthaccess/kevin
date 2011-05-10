<li>
	<% params['organisation'] = organisation.id %>
	<a href="${createLink(controller:controller, action:action, params:params)}">
		<span>${organisation.name}</span>
	</a>
	<g:if test="${organisation.children != null}">
		<ul>
			<g:each in="${organisation.children}" var="child">
				<g:render template="/templates/organisationTree" model="[ controller: controller, action: action, organisation: child, params:params]"/>
			</g:each>
		</ul>
	</g:if>
</li>