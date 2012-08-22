<%@ page import="org.chai.kevin.util.Utils" %>
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
				<g:topLevelReportTabs linkParams="${params}" exclude="${['fctTarget', 'indicators', 'reportType']}" />
				<g:if test="${fctTable != null}">
					<ul>
						<g:if test="${currentView == Utils.ReportType.MAP || (currentView == Utils.ReportType.TABLE && fctTable.hasData())}">
				            <li class="push-20">
				                <g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>
				                <g:render template="/templates/reportProgramParent"/>                            																
								<div class="selector">
									<g:reportTargetFilter linkParams="${params}" exclude="${['indicators']}"/>
									<g:render template="/fct/reportValueFilter"/>
								</div>
			              	</li>
		              	</g:if>
		              	
		              	<g:if test="${currentView == Utils.ReportType.MAP}">
			              	<g:render template="/maps/reportProgramMap" model="[linkParams:params]"/>
							<g:render template="/maps/reportProgramMapTable" 
									model="[linkParams:params, reportTable: fctTable, reportLocations: fctTable.locations, reportIndicators: fctTable.targetOptions]"/>
		              	</g:if>
		              	<g:elseif test="${currentView == Utils.ReportType.TABLE && fctTable.hasData()}">
		              		<g:render template="/fct/reportProgramTable" model="[linkParams:params]"/>							
							<li class="push-top-10 push-10">								
			                	<g:render template="/templates/reportTitle" model="[title: i18n(field:currentLocation.names), file: 'marker_small.png']"/>
			                	<g:render template="/templates/reportProgramParent"/>
			                	<div>
				                	<div>
				                		<g:message code="fct.report.datalocationtype"/>:
										<g:each in="${currentLocationTypes}" var="dataLocationType" status="i">						
											<g:i18n field="${dataLocationType.names}" /><g:if test="${i != currentLocationTypes.size()-1}">, </g:if>
										</g:each>
									</div>
									<g:render template="/fct/reportLocationBarChart" model="[linkParams:params]"/>
								</div>
				    		</li>
						</g:elseif>
						<g:else>
							<p class="nav-help"><g:message code="fct.report.table.noselection.label"/></p>	              	
						</g:else>	              			              	
		        	</ul>
	        	</g:if>
	        	<g:else>
	        		<p class="nav-help"><g:message code="fct.report.table.noselection.label"/></p>
	        	</g:else>
            </div>
		</div>
	</body>
</html>