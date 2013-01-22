<g:set var="parentLocation" value="${currentLocation}"/>
<g:if test="${currentLocation.collectsData()}">
	<g:set var="parentLocation" value="${currentLocation.location}"/>
</g:if>
<g:else>
	<g:set var="parentLocation" value="${currentLocation.parent}"/>
</g:else>
<g:if test="${parentLocation != null}">
    <% def parentLocationLinkParams = new HashMap(params) %>
	<% parentLocationLinkParams['location'] = parentLocation?.id+"" %>
	<a class="level-up" href="${createLink(controller:controllerName, action:actionName, params:parentLocationLinkParams)}">
	<g:message code="report.view.label" args="${[i18n(field: parentLocation.names)]}"/></a>		  
</g:if>