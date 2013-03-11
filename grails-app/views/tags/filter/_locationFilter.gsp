<div class="left">
	<%
		locationLinkParams = [:]
		locationLinkParams.putAll linkParams
		locationLinkParams.remove 'location'
	%>
	<span class="js_dropdown dropdown">
		<a class="location js_dropdown-link nice-button with-highlight" href="#" data-type="location">
			<g:if test="${currentLocation != null}">
				<g:i18n field="${currentLocation.names}"/>
			</g:if>
			<g:else>
				<g:message code="filter.location.noselection.label"/>
			</g:else> 
		</a>
		<div class="dropdown-list js_dropdown-list push-top-10">
			<g:if test="${!locationFilterTree.empty}">
				<ul>
					<g:render template="/tags/filter/locationTree"
						model="[
							controller: controllerName, 
							action: actionName,
							current: currentLocation,
							location: locationFilterRoot,
							locationFilterTree: locationFilterTree,				
							linkParams: locationLinkParams
						]" />
				</ul>
			</g:if>
			<g:else>
				<g:if test="${locationFilterRoot == null}">
					<div><g:message code="filter.location.no.locations"/></div>
				</g:if>
				<g:if test="${selectedTypes.empty}">
					<div><g:message code="filter.location.no.locationtype"/></div>
				</g:if>
				<g:else>
					<div><g:message code="filter.location.no.datalocations"/></div>
				</g:else>
			</g:else>
		</div>
	</span>
</div>