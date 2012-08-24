<g:if test="${currentLocation.parent != null}">
    <% def parentLocationLinkParams = new HashMap(params) %>
	<% parentLocationLinkParams['location'] = currentLocation.parent?.id+"" %>
	<a class="level-up" href="${createLink(controller:controllerName, action:actionName, params:parentLocationLinkParams)}">
	<g:message code="report.view.label" args="${[i18n(field: currentLocation.parent.names)]}"/></a>		  
</g:if>