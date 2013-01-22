<% def parentProgramLinkParams = new HashMap(linkParams) %>
<g:if test="${dashboardEntity.isTarget()}">
	<% parentProgramLinkParams['program'] = dashboardEntity.program.id+"" %>
	<a class="level-up" href="${createLink(controller:controllerName, action:actionName, params:parentProgramLinkParams)}">
	<g:message code="report.view.label" args="${[i18n(field: dashboardEntity.program.names)]}"/></a>	  
</g:if>
<g:elseif test="${currentProgram.parent != null}">
	<% parentProgramLinkParams['program'] = currentProgram.parent.id+"" %>
	<a class="level-up" href="${createLink(controller:controllerName, action:actionName, params:parentProgramLinkParams)}">
	<g:message code="report.view.label" args="${[i18n(field: currentProgram.parent.names)]}"/></a>	  
</g:elseif>