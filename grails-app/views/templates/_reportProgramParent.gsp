<g:if test="${currentProgram.parent != null}">
	<% def parentProgramLinkParams = new HashMap(params) %>
	<% parentProgramLinkParams['program'] = currentProgram.parent.id+"" %>
	<a class="level-up" href="${createLink(controller:controllerName, action:actionName, params:parentProgramLinkParams)}">
	<g:message code="report.view.label" args="${[i18n(field: currentProgram.parent.names)]}"/></a>	  
</g:if>