<%@ page import="java.text.SimpleDateFormat" %>
<% SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy"); %>
<%@ page import="org.chai.kevin.reports.ReportService.ReportType" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="dashboard.title" /></title>
		
		<!-- for admin forms -->
		<shiro:hasPermission permission="admin:dashboard">
			<r:require modules="form"/>
		</shiro:hasPermission>		  
		<r:require module="dashboard"/>
		<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	</head>
	<body>
		<div id="report">
			<div class="filter-bar">
				<g:render template="/templates/topLevelReportFilters" model="[linkParams:params]"/>
			</div>
			<div class="main">
				<g:topLevelReportTabs linkParams="${params}" exclude="${['dashboardEntity']}" />
				<g:if test="${dashboardTable != null}">
					<ul>
						<g:if test="${currentView == ReportType.MAP}">
							<g:render template="/dashboard/map"/>
						</g:if>
						<g:elseif test="${currentView == ReportType.TABLE && dashboardTable.hasData()}">
							<g:render template="/dashboard/table"/>								
						</g:elseif>
						<g:else>
							<p class="nav-help"><g:message code="report.table.noselection.label"/></p>
						</g:else>
					</ul>
				</g:if>
				<g:else>
					<p class="nav-help"><g:message code="report.table.noselection.label"/></p>
				</g:else>
			</div>		
		</div>	  
	</body>	   
</html>