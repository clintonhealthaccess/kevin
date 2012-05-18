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
			<div class="subnav">			
				<g:render template="/templates/topLevelReportFilters" model="[linkParams:params]"/>
			</div>
			<div class="main">
				<g:render template="/templates/topLevelReportTabs" model="[linkParams:params]"/>
				<g:render template="/templates/help" model="[content: i18n(field: currentProgram.descriptions)]"/>			
				<ul id="questions">
		            <li class="question push-20">
		                <g:render template="/templates/reportTableHeader" model="[table:'program', linkParams:params]"/>	                																
						<g:if test="${fctTable != null && fctTable.hasData()}">
							<g:render template="/fct/reportTargetFilter" model="[linkParams:params]"/>
	              			<g:render template="/fct/reportProgramTable" model="[linkParams:params]"/>
	              		</g:if>
	              		<g:else><div><g:message code="fct.report.table.noselection.label"/></div></g:else>	
	              	</li>	              	
	              	<li class="question push-10">
	                	<g:render template="/templates/reportTableHeader" model="[table:'location', linkParams:params]"/>
	                	<g:if test="${fctTable != null && fctTable.hasData()}">	                	              								
							<p><g:message code="fct.report.datalocationtype"/>: 
								<g:each in="${currentLocationTypes}" var="dataLocationType" status="i">						
									<g:i18n field="${dataLocationType.names}" />
									<g:if test="${i != currentLocationTypes.size()-1}">, </g:if>
								</g:each>
							</p>
							<br />
	              			<g:render template="/fct/reportLocationBarChart" model="[linkParams:params]"/>
	              		</g:if>
	              		<g:else><div><g:message code="fct.report.table.noselection.label"/></div></g:else>
			    	</li>			    	
	        	</ul>	
            </div>					
		</div>		
	</body>
</html>