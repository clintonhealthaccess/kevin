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
				<g:render template="/templates/topLevelReportFilters" model="[linkParams:params]"/>
			</div>
			<div class="main">			
				<g:topLevelReportTabs linkParams="${params}" exclude="${['dsrCategory']}" />							
				<g:if test="${dsrTable != null && dsrTable.hasData()}">					
					<ul>
						<li>
							<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names)+' x '+i18n(field:currentLocation.names), file: 'star_small.png']"/>                
							<g:render template="/dsr/reportCategoryFilter" model="[linkParams:params]"/>
						</li>
						<g:if test="${dsrTable.hasData()}">
							<g:render template="/dsr/reportProgramTable" model="[linkParams:params]"/>
						</g:if>
						<g:else>
							<g:message code="dsr.report.table.noselection.label"/>	              	
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