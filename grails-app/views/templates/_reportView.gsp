<%@ page import="org.chai.kevin.reports.ReportService.ReportType" %>
<%
	reportViewLinkParams = [:]
	reportViewLinkParams.putAll linkParams
	if(currentView == ReportType.MAP) 
		reportViewLinkParams['reportType'] = ReportType.TABLE.toString().toLowerCase()
	else 
		reportViewLinkParams['reportType'] = ReportType.MAP.toString().toLowerCase()
%>
<g:form method="get" url="${[controller:controllerName, action:actionName, params: reportViewLinkParams]}">	
	<button class="right" type="submit">
		<g:if test="${currentView == ReportType.MAP}">
			<g:message code="report.view.table"/>
		</g:if>
		<g:else>
			<g:message code="report.view.map"/>
		</g:else>
	</button>
</g:form>