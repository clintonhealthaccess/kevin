<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="fctTable.view.label" default="Facility Count Tables" /></title>
		
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
				<ul id='questions'>
	              <li class='question'>
	                <g:render template="/templates/reportTableHeader" model="[table:'location', linkParams:params]"/>	                
	              </li>
	            </ul>					
				<g:if test="${fctTable != null && fctTable.hasData()}">
					<div class='selector'>
						<g:render template="/fct/reportTargetFilter" model="[linkParams:params]"/>
	              		<g:render template="/fct/reportLocationTable" model="[linkParams:params]"/>
	              	</div>
              	</g:if>	              	
              	<g:else>
              		<g:message code="fct.report.table.noselection.label"/>	              	
              	</g:else>
				<ul id='questions'>
	              <li class='question'>
	                <g:render template="/templates/reportTableHeader" model="[table:'program', linkParams:params]"/>	                
	              </li>
	            </ul>				
				<g:if test="${fctTable != null && fctTable.hasData()}">
					<p>Facility types: 
						<g:each in="${currentLocationTypes}" var="locationType" status="i">						
							<g:i18n field="${locationType.names}" /><g:if test="${i != currentLocationTypes.size()-1}">, </g:if>
						</g:each>
					</p>
					<br />
	            	<g:render template="/fct/reportProgramTable" model="[linkParams:params]"/>
	            </g:if>
	            <g:else>
	            	<g:message code="fct.report.table.noselection.label"/>	              	
	            </g:else>	
            </div>					
		</div>		
	</body>
</html>