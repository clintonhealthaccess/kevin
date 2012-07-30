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
								
				<ul class="clearfix">
  					<li class="push-20">
						<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field: currentProgram.names), descriptions: i18n(field: currentProgram.names), file: 'star_small.png']"/>														
						<g:if test="${currentProgram.parent != null}">
							<% def parentProgramLinkParams = new HashMap(params) %>
							<% parentProgramLinkParams['program'] = currentProgram.parent.id+"" %>
							<a class="level-up" href="${createLink(controller:'dashboard', action:'view', params:parentProgramLinkParams)}">
								<g:message code="report.view.label" args="${[i18n(field: currentProgram.parent.names)]}"/></a>	  
					  	</g:if>
												
						<g:if test="${programDashboard != null && programDashboard.hasData()}">
							<g:render template="/dashboard/reportCompareFilter" model="[table:'program', dashboard:programDashboard]"/>
							<div class="horizontal-graph-wrap">
								<g:render template="/dashboard/reportProgramTable" model="[dashboard:programDashboard]"/>
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
		                <g:if test="${currentLocation.parent != null}">
		                	<% def parentLocationLinkParams = new HashMap(params) %>
							<% parentLocationLinkParams['location'] = currentLocation.parent?.id+"" %>
							<a class="level-up" href="${createLink(controller:'dashboard', action:'view', linkParams:parentLocationLinkParams)}">
							<g:message code="report.view.label" args="${[i18n(field: currentLocation.parent.names)]}"/></a>		  
						</g:if>
		                						
		                <g:if test="${locationDashboard != null && locationDashboard.hasData()}">
		                <g:render template="/dashboard/reportCompareFilter" model="[table:'location', dashboard:locationDashboard]"/>
							<div class="horizontal-graph-wrap">
								<g:render template="/dashboard/reportLocationTable" model="[dashboard:locationDashboard]"/>			                  
							</div>
		                </g:if>
		                <g:else>
		                	<div class="horizontal-graph-wrap">
		                		<g:message code="dashboard.report.table.noselection.label"/>
		                	</div>
		                </g:else>
		            </li>
		        </ul>    
			</div>    	
	    </div>    
    </body>    
</html>