<%@ page import="java.text.SimpleDateFormat" %>
<% SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy"); %>

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
				<g:topLevelReportTabs linkParams="${params}" exclude="${['dashboardEntity', 'reportType']}" />
	
				<g:if test="${dashboard!=null}">			
					<ul class="clearfix">
						<li class="push-20">
							<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field: currentProgram.names), descriptions: i18n(field: currentProgram.names), file: 'star_small.png']"/>														
							<g:render template="/templates/reportProgramParent"/>												
							<g:if test="${dashboard.hasData()}">
								<g:render template="/dashboard/reportCompareFilter" model="[table:'program', locationPath: dashboard.locationPath - currentLocation]"/>
								<div class="horizontal-graph-wrap">
									<g:render template="/dashboard/reportProgramTable" model="[dashboard:dashboard]"/>
								</div>
							</g:if>
							<g:else>
								<div class="horizontal-graph-wrap">
									<g:message code="dashboard.report.table.noselection.label"/>
								</div>
							</g:else>
						</li>
						<li class="push-10">
							<g:render template="/templates/reportTitle" model="[title: i18n(field: currentLocation.names), file: 'marker_small.png']"/>
							<g:render template="/templates/reportLocationParent"/>				
							<g:if test="${dashboard.hasData()}">
								<g:render template="/dashboard/reportCompareFilter" model="[table:'location', locationPath: dashboard.locationPath]"/>
								<div class="horizontal-graph-wrap">
									<g:render template="/dashboard/reportLocationTable" model="[dashboard:dashboard]"/>							  
								</div>
							</g:if>
							<g:else>
								<div class="horizontal-graph-wrap">
									<g:message code="dashboard.report.table.noselection.label"/>
								</div>
							</g:else>
						</li>
					</ul> 
				</g:if>
				<g:else>
					<p class="nav-help"><g:message code="dashboard.report.table.noselection.label"/></p>
				</g:else>
			</div>		
		</div>	  
	</body>	   
</html>