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
				<g:topLevelReportTabs linkParams="${params}" exclude="${['fctTarget', 'reportType']}" />								
				<g:if test="${fctTable != null && fctTable.hasData()}">								
					<ul>
			            <li class="push-20">
			                <g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>
			                <g:if test="${currentProgram.parent != null}">
								<% def parentProgramLinkParams = new HashMap(params) %>
								<% parentProgramLinkParams['program'] = currentProgram.parent.id+"" %>
								<a class="level-up" href="${createLink(controller:'fct', action:'view', params:parentProgramLinkParams)}">
									<g:message code="report.view.label" args="${[i18n(field: currentProgram.parent.names)]}"/></a>	  
						  	</g:if>	                             																
							<div class="selector">
								<g:render template="/fct/reportTargetFilter" model="[linkParams:params]"/>
								<g:render template="/fct/reportValueFilter"/>
							</div>
		              		<g:render template="/fct/reportProgramTable" model="[linkParams:params]"/>	
		              	</li>
		              	<li class="push-10">
		                	<g:render template="/templates/reportTitle" model="[title: i18n(field:currentLocation.names), file: 'marker_small.png']"/>
		                	<g:if test="${currentLocation.parent != null}">
			                	<% def parentLocationLinkParams = new HashMap(params) %>
								<% parentLocationLinkParams['location'] = currentLocation.parent?.id+"" %>
								<a class="level-up" href="${createLink(controller:'fct', action:'view', linkParams:parentLocationLinkParams)}">
								<g:message code="report.view.label" args="${[i18n(field: currentLocation.parent.names)]}"/></a>		  
							</g:if>
		                	<div>
			                	<div>
			                		<g:message code="fct.report.datalocationtype"/>: 
									<g:each in="${currentLocationTypes}" var="dataLocationType" status="i">						
										<g:i18n field="${dataLocationType.names}" />
										<g:if test="${i != currentLocationTypes.size()-1}">, </g:if>
									</g:each>
								</div>								
								<g:render template="/fct/reportLocationBarChart" model="[linkParams:params]"/>
							</div>
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