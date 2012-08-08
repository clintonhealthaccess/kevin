<%@ page import="org.chai.kevin.util.Utils" %>
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
				<g:render template="/templates/topLevelReportFilters"/>
			</div>
			<div class="main">			
				<g:topLevelReportTabs linkParams="${params}" exclude="${['dsrTarget', 'dsrCategory', 'reportType']}" />					
				<g:if test="${dsrTable != null}">
					<ul>
						<g:if test="${currentView == Utils.ReportType.MAP || (currentView == Utils.ReportType.TABLE && dsrTable.hasData())}">
							<li>
								<g:render template="/templates/reportTitle" model="[program: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>
								<g:if test="${currentProgram.parent != null}">
									<% def parentProgramLinkParams = new HashMap(params) %>
									<% parentProgramLinkParams['program'] = currentProgram.parent.id+"" %>
									<a class="level-up" href="${createLink(controller:'dsr', action:'view', params:parentProgramLinkParams)}">
										<g:message code="report.view.label" args="${[i18n(field: currentProgram.parent.names)]}"/></a>	  
							  	</g:if>
								<g:reportView linkParams="${params}" exclude="${['dsrTarget']}"/>
								<g:render template="/dsr/reportCategoryFilter" model="[linkParams:params]"/>												
							</li>
						</g:if>
						
						<g:if test="${currentView == Utils.ReportType.MAP}">
							<g:render template="/dsr/reportProgramMap" model="[linkParams:params]"/>
							<g:render template="/dsr/reportProgramMapTable" model="[linkParams:params]"/>
						</g:if>
						<g:elseif test="${currentView == Utils.ReportType.TABLE && dsrTable != null && dsrTable.hasData()}">
							<g:render template="/dsr/reportProgramTable"/>
						</g:elseif>
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