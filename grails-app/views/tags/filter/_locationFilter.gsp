<div class="filter">
	<span class="js_dropdown dropdown">
		<g:if test="${currentLocation != null}">
			<a class="location selected" href="#" data-type="location" data-location="${currentLocation.id}">
				<g:i18n field="${currentLocation.names}"/>
			</a>
		</g:if>
		<g:else>
			<a class="location selected" href="#" data-type="location">
				<g:message code="filter.location.noselection.label" default="no location selected"/>
			</a>
		</g:else> 
		<div class="hidden dropdown-list js_dropdown-list">
			<g:if test="${locationTree != null && !locationTree.empty}">
				<ul>
					<g:render template="/templates/locationTree" 
						model="[
							controller: controllerName, 
							action: actionName,
							current: currentLocation,
							location: locationRoot,
							locationTree: locationTree,				
							linkParams: linkParams
						]" />
				</ul>
			</g:if>
		</div>
	</span>
</div>