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
				<g:if test="${fctTable != null && fctTable.hasData()}">								
					<ul>
			            <li class="push-20">
			                <g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>		                             																
							<div class="selector">
								<g:render template="/fct/reportTargetFilter" model="[linkParams:params]"/>
								<g:render template="/fct/reportValueFilter"/>
							</div>
		              		<g:render template="/fct/reportProgramTable" model="[linkParams:params]"/>	
		              	</li>
		              	<li class="push-10">
		                	<g:render template="/templates/reportTitle" model="[title: i18n(field:currentLocation.names), file: 'marker_small.png']"/>
		                	<div>
			                	<div>
			                		<g:message code="fct.report.datalocationtype"/>: 
									<g:each in="${currentLocationTypes}" var="dataLocationType" status="i">						
										<g:i18n field="${dataLocationType.names}" />
										<g:if test="${i != currentLocationTypes.size()-1}">, </g:if>
									</g:each>
								</div>
								<!-- chart legend -->
								<ul class="horizontal chart_legend">
									<g:if test="${fctTable != null && fctTable.targetOptions != null && !fctTable.targetOptions.empty}">
										<g:each in="${fctTable.targetOptions}" var="targetOption" status="i">
											<li><span class="bar${i+1}"></span> <g:i18n
													field="${targetOption.names}" /></li>
										</g:each>
									</g:if>
								</ul>
							</div>
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