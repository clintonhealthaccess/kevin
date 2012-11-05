<%
	newLinkParams = [:]
	newLinkParams.putAll linkParams
%>
<div class="right">
	<a class="switch" href="${createLink(controller: controllerName, action: 'export', params: newLinkParams)}">
	<g:message code="report.view.download.label" /></a>
</div>