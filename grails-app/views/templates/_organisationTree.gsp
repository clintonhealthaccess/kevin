<r:require module="foldable" />

<li class="${current?.id == location?.id ?'current':''} foldable ${location?.level==1 ?'opened':''}">
	<g:if test="${location.children != null && !location.children.empty}">
		<a class="foldable-toggle" href="#">(toggle)</a>
	</g:if>
		<% def locationLinkParams = new HashMap(linkParams) %>
		<% locationLinkParams['location'] = location.id+"" %>	
		<a class="js_dropdown-link dropdown-link parameter" data-type="location"
			data-location="${location.id}"
			href="${createLinkByFilter(controller:controller, action:action, params:locationLinkParams)}">
			<g:i18n field="${location.names}" />
		</a>
	<g:if test="${location.children != null && !location.children.empty}">
		<ul class="location-fold"
			id="location-fold-${location.id}">
			<g:each in="${location.children}" var="child">
				<g:render template="/templates/locationTree"
					model="[controller: controller, 
					action: action,
					current: current,
					location: child,					
					linkParams:linkParams]" />
			</g:each>
		</ul>
	</g:if>
</li>