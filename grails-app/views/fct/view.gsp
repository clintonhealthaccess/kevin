<%@ page import="org.chai.kevin.reports.ReportService.ReportType" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="fct.title" /></title>
		
		<!-- for admin forms -->
		<shiro:hasPermission permission="admin:fct">
        	<r:require modules="form"/>
        </shiro:hasPermission>        
		<r:require modules="fct"/>

	</head>
	<body>
		<div id="report">
			<div class="filter-bar">
				<g:render template="/templates/topLevelReportFilters" model="[linkParams:params]"/>
			</div>
			<div class="main">
				<g:topLevelReportTabs linkParams="${params}" exclude="${['fctTarget']}" />
				<g:if test="${fctTable != null}">
					<ul>
						<g:if test="${currentView == ReportType.MAP}">
							<g:render template="/fct/map"/>
						</g:if>
						<g:elseif test="${currentView == ReportType.TABLE && fctTable.hasData()}">
							<g:render template="/fct/table"/>								
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