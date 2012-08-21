<%@ page import="org.chai.kevin.util.Utils" %>
<%
	newLinkParams = [:]
	newLinkParams.putAll linkParams
	if(currentView == Utils.ReportType.MAP) 
		newLinkParams['reportType'] = Utils.ReportType.TABLE.toString().toLowerCase()
	else 
		newLinkParams['reportType'] = Utils.ReportType.MAP.toString().toLowerCase()
%>
<g:form method="get" url="[controller:controllerName, action:actionName, params: newLinkParams]">
	<button class="right" type="submit">
		<g:if test="${currentView == Utils.ReportType.MAP}">
			<g:message code="report.view.table"/>
		</g:if>
		<g:else>
			<g:message code="report.view.map"/>
		</g:else>
	</button>
</g:form>