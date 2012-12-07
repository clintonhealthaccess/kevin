<%@ page import="org.chai.kevin.util.Utils" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="dsr.title" /></title>
		
		<!-- for admin forms -->
		<shiro:hasPermission permission="admin:dsr">
        	<r:require modules="form"/>
        </shiro:hasPermission>        
		<r:require modules="dsr"/>
		
	</head>
	<body>
		<div id="report">
			<div class="filter-bar">
				<g:render template="/templates/topLevelReportFilters"/>
			</div>
			<div class="main">
				<!-- TODO get rid of reportType from exclude list when all maps are implemented -->
				<g:topLevelReportTabs linkParams="${params}" exclude="${['dsrCategory', 'indicators', 'reportType']}" />
				<g:if test="${dsrTable != null}">
					<ul>
						<g:if test="${currentView == Utils.ReportType.MAP}">
							<g:render template="/dsr/map"/>
						</g:if>
						<g:elseif test="${currentView == Utils.ReportType.TABLE && dsrTable.hasData()}">
							<g:render template="/dsr/table"/>								
						</g:elseif>
						<g:else>
							<p class="nav-help"><g:message code="dsr.report.table.noselection.label"/></p>
						</g:else>
					</ul>
				</g:if>
				<g:else>
					<p class="nav-help"><g:message code="dsr.report.table.noselection.label"/></p>
				</g:else>
			</div>		
		</div>		
	</body>	
</html>