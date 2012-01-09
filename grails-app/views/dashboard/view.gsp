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
		<div id="dashboard">
			<div id="corner" class="subnav">
				<g:iterationFilter linkParams="${params}"/>
			</div>
		
			<g:render template="/templates/facilityTypeFilter" model="[facilityTypes: facilityTypes, currentFacilityTypes: currentFacilityTypes, linkParams:params]"/>
			
			<div id="top" class="main">
	    		<h3 class="form-heading"><g:message code="dashboard.labels.objectives" default="Objectives"/></h3>
		    	<ul class="form-heading-list horizontal inline-list">
		    		<g:each in="${dashboard.objectivePath}" var="objective">
			    		<li>
			    			<g:link controller="dashboard" action="view" params="[period: currentPeriod.id, objective: objective.id, organisation: currentOrganisation.id]"><g:i18n field="${objective.names}"/></g:link>
			    		</li>
		    		</g:each>
		    		<li>
		    			<g:i18n field="${currentObjective.names}"/>
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
					    			<g:link controller="dashboard" action="view" params="[period: currentPeriod.id, objective: currentObjective.id, organisation: organisation.id]"><g:i18n field="${organisation.names}"/></g:link>
					    		</li>
				    		</g:each>
				    		<li>
				    			<g:i18n field="${currentOrganisation.names}"/>
				    		</li>
				    	</ul>
			    	</div>
		    	</div>
		    
		    	<div class="box">
		    		<div id="values">
				    	<table class="listing">	
				    		<thead class="header">
					    		<tr class="row">
					    			<th class="cell label">&nbsp;</th>
					    			<g:each in="${dashboard.objectiveEntries}" var="objectiveEntry">
						    			<g:set var="objective" value="${objectiveEntry.entry}"/>
						    			<th class="cell label top col-${objective.id}" data-col="${objective.id}">
						    				<div><span>
						    				<g:if test="${!objective.isTarget()}">
												<a class="cluetip" 
													title="${i18n(field:objective.names)}"
													href="${createLink(controller:'dashboard', action:'view', params:[period: currentPeriod.id, objective: objective.id, organisation: currentOrganisation.id])}"
												   	rel="${createLink(controller:'dashboard', action:'getDescription', id:objective.id)}">
													<g:i18n field="${objective.names}"/>
												</a>
											</g:if>
											<g:else>
												<a 	class="no-link cluetip" href="#" onclick="return false;" 
													title="${i18n(field:objective.names)}"
													rel="${createLink(controller:'dashboard', action:'getDescription', id:objective.id)}">
													<g:i18n field="${objective.names}"/>
												</a>
											</g:else>
			
								    		<shiro:hasPermission permission="admin:dashboard">
							    				<g:if test="${!objective.isTarget()}">
													<span>
														<a class="edit-link" href="${createLinkWithTargetURI(controller:'dashboardObjective',action:'edit', id:objectiveEntry.id)}"><g:message code="default.link.edit.label" default="Edit" /></a>
													</span>
												</g:if>
												<g:else>
													<span>
														<a class="edit-link" href="${createLinkWithTargetURI(controller:'dashboardTarget',action:'edit', id:objectiveEntry.id)}"><g:message code="default.link.edit.label" default="Edit" /></a>
													</span>
												</g:else>
												<g:if test="${!objective.hasChildren()}">
													<g:if test="${!objective.isTarget()}">
														<span>
															<a class="delete-link" href="${createLinkWithTargetURI(controller:'dashboardObjective',action:'delete', id:objectiveEntry.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
																<g:message code="default.link.delete.label" default="Delete" />
															</a>
														</span>
													</g:if>
													<g:else>
														<span>
															<a class="delete-link" href="${createLinkWithTargetURI(controller:'dashboardTarget',action:'delete', id:objectiveEntry.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
																<g:message code="default.link.delete.label" default="Delete" />
															</a>
														</span>
													</g:else>
												</g:if>
											</shiro:hasPermission>
												
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
											<g:link controller="dashboard" action="view" params="[period: currentPeriod.id, objective: currentObjective.id, organisation: organisation.id]"><g:i18n field="${organisation.names}"/></g:link>
										</g:if>
										<g:else>
											<g:i18n field="${organisation.names}"/>
										</g:else>
										</span></div>
									</td>
									<g:each in="${dashboard.objectiveEntries}" var="objectiveEntry">
										<g:set var="objective" value="${objectiveEntry.entry}"/>
										<g:set var="percentage" value="${dashboard.getPercentage(organisation, objective)}"/>
										<td class="highlighted value cell row-${organisation.id} col-${objective.id}" data-row="${organisation.id}" data-col="${objective.id}">
											<g:if test="${percentage!=null}">
												<div style="background-color: ${percentage.color};">
												    <span>
												    	<a class="no-link" href="${createLink(controller:'dashboard', action:'explain', params:[objective: objective.id, organisation: organisation.id, period: currentPeriod.id])}">
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
												<div class="">&nbsp;</div>
											</g:else>
										</td>
									</g:each></tr>
									<tr class="explanation-row">
										<g:each in="${dashboard.objectiveEntries}" var="objectiveEntry">
											<g:set var="objective" value="${objectiveEntry.entry}"/>
											<td class="explanation-cell" id="explanation-${organisation.id}-${objective.id}"></td>
										</g:each>
									</tr>
								</g:each>
							</tbody>
							<!-- body -->
				    	</table>
				    	
				    	<!-- ADMIN SECTION -->
			    		<shiro:hasPermission permission="admin:dashboard">
			    			<div class="float-right">
								<div><a class="add-row" href="${createLinkWithTargetURI(controller:'dashboardTarget', action:'create', params:[currentObjective: currentObjective.id])}"><g:message code="default.add.label" args="[message(code:'dashboard.target.label')]" default="Add target"/></a></div>
								<div><a class="add-row" href="${createLinkWithTargetURI(controller:'dashboardObjective', action:'create', params:[currentObjective: currentObjective.id])}"><g:message code="default.add.label" args="[message(code:'dashboard.objective.label')]" default="Add objective"/></a></div>
							</div>
						</shiro:hasPermission>
				    	<!-- ADMIN SECTION END -->
				    	
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
    				var objective = $(this).data('col');
    				explanationClick(this, organisation+'-'+objective, function(){});
    				return false;
    			});
    			
    		});
    	</r:script>
    	<!-- explanation -->
    </body>
</html>