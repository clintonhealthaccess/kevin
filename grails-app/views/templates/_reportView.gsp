<%@ page import="org.chai.kevin.util.Utils" %>
<g:if test="${currentView == Utils.ReportType.MAP}">
	<%
		newLinkParams = [:]
		newLinkParams.putAll linkParams
		newLinkParams['reportType'] = Utils.ReportType.TABLE.toString().toLowerCase()
	%>
	<a class="right switch" 
	href="${createLink(controller: 'dsr', action: 'view', params: newLinkParams)}">
	<g:message code="report.view.table"/>
	</a>
</g:if>
<g:else>
	<%
		newLinkParams = [:]
		newLinkParams.putAll linkParams
		newLinkParams['reportType'] = Utils.ReportType.MAP.toString().toLowerCase()
	%>
	<a class="right switch" 
	href="${createLink(controller: 'dsr', action: 'view', params: newLinkParams)}">
	<g:message code="report.view.map"/>
	</a>
</g:else>