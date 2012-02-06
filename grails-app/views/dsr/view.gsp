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
				<g:render template="/templates/topLevelReportFilters" model="[tab:'dsr', linkParams:params, params:params]"/>
			</div>
			<div class="main">
				<g:render template="/templates/topLevelReportTabs" model="[tab:'dsr', linkParams:params]"/>
				<g:render template="/templates/reportTabHelp" model="[params:params]"/>			
				<ul id='questions'>
	              <li class='question'>
	                <g:render template="/templates/reportTableHeader" model="[tab:'dsr']"/>	                
	                <g:render template="/templates/dsr/reportCategoryFilter" model="[params:params]"/>
	              </li>
	              <g:if test="${dsrTable != null}">             
	              	<g:render template="/templates/dsr/reportProgramTable" model="[params:params]"/>
	              </g:if>
				</ul>
			</div>		
		</div>		
	</body>	
</html>