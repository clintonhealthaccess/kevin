<r:require module="foldable" />

<li class="${current?.id == organisation?.id ?'current':''} foldable ${organisation?.level==1 ?'opened':''}">
	<g:if test="${organisation.children != null && !organisation.children.isEmpty()}">
		<a class="foldable-toggle" href="#">(toggle)</a>
	</g:if>
		<% def locationLinkParams = new HashMap(linkParams) %>
		<% locationLinkParams['organisation'] = organisation.id+"" %>	
		<a class="dropdown-link parameter" data-type="organisation"
			data-organisation="${organisation.id}"
			href="${createLinkByFilter(controller:controller, action:action, params:locationLinkParams)}">
			<g:i18n field="${organisation.names}" />
		</a>
	<g:if test="${organisation.children != null && !organisation.children.isEmpty()}">
		<ul class="organisation-fold"
			id="organisation-fold-${organisation.id}">
			<g:each in="${organisation.children}" var="child">
				<g:render template="/templates/organisationTree"
					model="[controller: controller, 
					action: action,
					current: current,
					organisation: child,					
					linkParams:linkParams]" />
			</g:each>
		</ul>
	</g:if>
</li>