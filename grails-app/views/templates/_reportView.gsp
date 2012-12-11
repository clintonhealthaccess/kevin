<%@ page import="org.chai.kevin.reports.ReportService.ReportType" %>
<%
	newLinkParams = [:]
	newLinkParams.putAll linkParams
	if(currentView == ReportType.MAP) 
		newLinkParams['reportType'] = ReportType.TABLE.toString().toLowerCase()
	else 
		newLinkParams['reportType'] = ReportType.MAP.toString().toLowerCase()
%>
<g:form method="get" url="[controller:controllerName, action:actionName, params: newLinkParams]">
	<button class="right" type="submit">
		<g:if test="${currentView == ReportType.MAP}">
			<g:message code="report.view.table"/>
		</g:if>
		<g:else>
			<g:message code="report.view.map"/>
		</g:else>
	</button>
</g:form>