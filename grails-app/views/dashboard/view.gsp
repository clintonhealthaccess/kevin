<%@ page import="java.text.SimpleDateFormat" %>
<% SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy"); %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="dashboard.view.label" default="Dashboard" /></title>
        
        <!-- for admin forms -->
        <shiro:hasPermission permission="admin:dashboard">
        	<r:require modules="form"/>
        </shiro:hasPermission>
        
        <r:require module="dashboard"/>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    </head>
    <body>
		<div id="report">
			<div class="subnav">			
				<g:render template="/templates/topLevelReportFilters" model="[reportTab:'dashboard', linkParams:params]"/>
			</div>
			<div class="main">
				<g:render template="/templates/topLevelReportTabs" model="[reportTab:'dashboard', linkParams:params]"/>
			</div>
			
			<div class="main">
	    		<h3 class="form-heading"><g:message code="dashboard.labels.objectives" default="Objectives"/></h3>		    	
		    	<ul class="form-heading-list horizontal inline-list">
		    		<g:if test="${!dashboard.objectivePath.isEmpty()}">
			    		<g:each in="${dashboard.objectivePath}" var="objective">
				    		<li>
				    			<g:link controller="dashboard" action="view" params="[period: currentPeriod.id, dashboardEntity: objective.id, organisation: currentOrganisation.id]">
				    			<g:i18n field="${objective.names}"/></g:link>
				    		</li>
			    		</g:each>
		    		</g:if>
		    		<li>
		    			<g:i18n field="${dashboardEntity.names}"/>
		    		</li>
		    	</ul>
	    	</div>
	    	
	    	<div id="bottom" class="main">
	    		<div class="no-margin">
			    	<div class="left">
			    		<div class="italic"><g:message code="dashboard.labels.organisations" default="Organisations"/></div>
				    	<ul class="horizontal">
				    		<g:each in="${dashboard.organisationPath}" var="organisation">
					    		<li>
					    			<g:i18n field="${currentOrganisation.names}"/>
					    		</li>
					    	</g:each>
					    	</ul>
				    	</div>
			    	</div>
		    	</div>
		    
		    	<div class="box">
		    		<div id="values">
				    	<table class="listing">	
				    		<thead class="header">
					    		<tr class="row">
					    			<th class="cell label">&nbsp;</th>
					    			<g:each in="${dashboard.dashboardEntities}" var="entity">
						    			<th class="cell label top col-${entity.id}" data-col="${entity.id}">
						    				<div><span>
						    				<g:if test="${!entity.isTarget()}">
												<a class="cluetip" 
													title="${i18n(field:entity.names)}"
													href="${createLink(controller:'dashboard', action:'view', params:[period: currentPeriod.id, dashboardEntity: entity.id, organisation: currentOrganisation.id])}"
												   	rel="${createLink(controller:'dashboard', action:'getDescription', id:entity.id)}">
													<g:i18n field="${entity.names}"/>
												</a>
											</g:if>
											<g:else>
												<a 	class="no-link cluetip" href="#" onclick="return false;" 
													title="${i18n(field:entity.names)}"
													rel="${createLink(controller:'dashboard', action:'getDescription', id:entity.id)}">
													<g:i18n field="${entity.names}"/>
												</a>
											</g:else>												
											</span></div>
										</th>
									</g:each>
								</tr>
							</thead>
							<tbody class="body">
								<g:each in="${dashboard.organisations}" var="organisation">
								<tr class="row organisation">
									<td class="cell label row-${organisation.id}" data-row="${organisation.id}">
										<div><span>
										<g:if test="${!organisation.collectsData()}">
											<g:link controller="dashboard" action="view" params="[period: currentPeriod.id, dashboardEntity: dashboardEntity.id, organisation: organisation.id]"><g:i18n field="${organisation.names}"/></g:link>
										</g:if>
										<g:else>
											<g:i18n field="${organisation.names}"/>
										</g:else>
										</span></div>
									</td>
									<g:each in="${dashboard.dashboardEntities}" var="entity">
										<g:set var="percentage" value="${dashboard.getPercentage(organisation, entity)}"/>
										<td class="highlighted value cell row-${organisation.id} col-${entity.id}" data-row="${organisation.id}" data-col="${entity.id}">
											<g:if test="${percentage!=null}">
												<div style="background-color: ${percentage.color};">
												    <span>
												    	<a class="no-link" href="${createLink(controller:'dashboard', action:'explain', params:[dashboardEntity: entity.id, organisation: organisation.id, period: currentPeriod.id])}">
													    	<g:if test="${percentage.valid}">
																${percentage.roundedValue}%
															</g:if>
															<g:else>
																N/A
															</g:else>
												    	</a>
												    </span>
												</div>
											</g:if>
											<g:else>
												<g:i18n field="${organisation.names}"/>
											</g:else>
											</span></div>
										</td>
									</g:each></tr>
									<tr class="explanation-row">
										<g:each in="${dashboard.dashboardEntities}" var="entity">
											<td class="explanation-cell" id="explanation-${organisation.id}-${entity.id}"></td>
										</g:each>
									</tr>
								</g:each>
							</tbody>
							<!-- body -->
				    	</table>				
				    	
			    	</div>
			    </div>
			    <!-- center -->
	    	</div>
	    </div>
	
    	<!-- dashboard specific functionality -->
    	<r:script>
    		$(document).ready(function() {
    		
    			/**
    			 * dashboard
    			 **/
    			$('.cell.value').bind('click', function() {
    				var organisation = $(this).data('row');
    				var dashboardEntity = $(this).data('col');
    				explanationClick(this, organisation+'-'+dashboardEntity, function(){});
    				return false;
    			});
    			
    		});
    	</r:script>
    	<!-- explanation -->
    </body>
</html>