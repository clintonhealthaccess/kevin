<%@ page import="org.chai.kevin.reports.ReportService.ReportType" %>
<%
	reportExportLinkParams = [:]
	reportExportLinkParams.putAll linkParams
	reportExportLinkParams['reportType'] = ReportType.TABLE.toString().toLowerCase()
%>
<div class="right">
	<a class="switch" href="${createLink(controller: controllerName, action: 'export', params: reportExportLinkParams)}">
	<g:message code="report.view.download.label" /></a>
</div>