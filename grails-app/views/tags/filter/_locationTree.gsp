<r:require module="foldable" />
<li class="${current?.id == location?.id ?'current':''} foldable ${location?.level==1 ?'opened':''}">
	<% def locationLinkParams = new HashMap(linkParams) %>
	<% locationLinkParams['location'] = location.id+"" %>
	<g:if test="${location.children != null && !location.children.empty && !locationTree.disjoint(location.children)}">
		<a class="foldable-toggle" href="#">(toggle)</a>
	</g:if>
	<a class="dropdown-link js_dropdown-link parameter" data-type="location"
		data-location="${location.id}"
		href="${createLinkByFilter(controller:controller, action:action, params:locationLinkParams)}">
		<g:i18n field="${location.names}" />
	</a>
	<g:if test="${location.children != null && !location.children.empty}">		
		<g:each in="${location.children}" var="child">
			<g:if test="locationTree.contains(child)">
				<ul class="location-fold" id="location-fold-${location.id}">
					<g:render template="/templates/locationTree"
						model="[controller: controller, 
						action: action,
						current: current,
						location: child,					
						linkParams:linkParams]" />
				</ul>
			</g:if>
		</g:each>	
	</g:if>
</li>