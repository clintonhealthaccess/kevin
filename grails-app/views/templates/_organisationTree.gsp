<r:require module="foldable"/>

<li class="${current?.id == organisation.id?'current':''} foldable ${organisation.level==1?'opened':''}">
	
	<g:if test="${organisation.children != null}">
		<a class="foldable-toggle" href="#">(toggle)</a>
	</g:if>
	<g:if test="${organisation.level < displayLinkUntil}">
		<span>${organisation.name}</span>
	</g:if>
	<g:else>
		<% def newLinkParams = new HashMap(linkParams) %>
		<% newLinkParams['organisation'] = organisation.id %>	
		<a class="dropdown-link parameter" data-type="organisation" data-organisation="${organisation.id}" href="${createLinkByFilter(controller:controller, action:action, params:newLinkParams)}">
			${organisation.name}
		</a>
	</g:else>
	<g:if test="${organisation.children != null}">
		<ul class="organisation-fold" id="organisation-fold-${organisation.id}">
			<g:each in="${organisation.children}" var="child">
				<g:render template="/templates/organisationTree" model="[controller: controller, action: action, organisation: child, current: current, linkLevel: linkLevel, displayLinkUntil: displayLinkUntil, linkParams:linkParams]"/>
			</g:each>
		</ul>
	</g:if>
</li>