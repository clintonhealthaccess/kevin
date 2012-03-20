<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="dsrTable.view.label" default="District Summary Reports" /></title>
		
		<!-- for admin forms -->
		<shiro:hasPermission permission="admin:dsr">
        	<r:require modules="form"/>
        </shiro:hasPermission>        
		<r:require modules="dsr"/>
		
	</head>
	<body>
		<div id="report">
			<div class="subnav">			
				<g:render template="/templates/topLevelReportFilters" model="[linkParams:params]"/>
			</div>
			<div class="main">
				<g:render template="/templates/topLevelReportTabs" model="[linkParams:params]"/>
				<g:render template="/templates/help" model="[content: i18n(field: currentProgram.descriptions)]"/>			
				<ul id="questions">
	              <li class="question">
	                <g:render template="/templates/reportTableHeader" model="[linkParams:params]"/>	                
	                <g:render template="/dsr/reportCategoryFilter" model="[linkParams:params]"/>
	              </li>
	              <g:if test="${dsrTable.hasData()}">
	              	<g:render template="/dsr/reportProgramTable" model="[linkParams:params]"/>
	              </g:if>
	              <g:else>
	              	<g:message code="dsr.report.table.noselection.label"/>	              	
	              </g:else>
				</ul>
			</div>		
		</div>		
	</body>	
</html>