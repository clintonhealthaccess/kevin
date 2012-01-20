<div class="filter">
	<span class="bold"><g:message code="filter.location.label"/></span>
	<span class="dropdown subnav-dropdown">
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
		<div class="hidden dropdown-list">
			<ul>
				<g:render template="/templates/locationTree" model="[
					controller: controllerName, 
					action: actionName,
					current: currentLocation,
					location: locationRoot,					
					linkParams: linkParams
				]" />
			</ul>
		</div>
	</span>
</div>