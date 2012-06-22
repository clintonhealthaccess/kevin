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
				<g:render template="/templates/topLevelReportTabs" model="[linkParams:params]"/>				
				<g:if test="${fctTable != null && fctTable.hasData()}">
					<g:render template="/templates/help" model="[content: currentDescriptions]"/>			
					<ul>
			            <li class="push-20">
			                <g:render template="/templates/reportTitle" model="[title: i18n(field:currentProgram.names), file: 'star_small.png']"/>			                             																
							<g:render template="/fct/reportTargetFilter" model="[linkParams:params]"/>
		              		<g:render template="/fct/reportProgramTable" model="[linkParams:params]"/>	
		              	</li>
		              	<li class="push-10">
		                	<g:render template="/templates/reportTitle" model="[title: i18n(field:currentLocation.names), file: 'marker_small.png']"/>
		                	<p>
		                		<g:message code="fct.report.datalocationtype"/>: 
								<g:each in="${currentLocationTypes}" var="dataLocationType" status="i">						
									<g:i18n field="${dataLocationType.names}" />
									<g:if test="${i != currentLocationTypes.size()-1}">, </g:if>
								</g:each>
							</p>
							<br />
		              		<g:render template="/fct/reportLocationBarChart" model="[linkParams:params]"/>
				    	</li>
		        	</ul>
	        	</g:if>
	        	<g:else>
	        		<p class="nav-help"><g:message code="fct.report.table.noselection.label"/></p>
	        	</g:else>
            </div>					
		</div>		
	</body>
</html>