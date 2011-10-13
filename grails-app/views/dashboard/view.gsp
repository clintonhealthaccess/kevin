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
			<div id="corner" class="box">
				<span class="bold"><g:message code="dashboard.labels.iteration" default="Iteration"/></span>
				<div class="dropdown white-dropdown">
					<a class="selected" href="#"><g:dateFormat format="yyyy" date="${currentPeriod.startDate}"/></a>
					<div class="hidden dropdown-list">
						<ul>
							<g:each in="${periods}" var="period">
								<li>
									<a href="${createLink(controller:'dashboard', action:'view', params:[period: period.id, objective: currentObjective.id, organisation: currentOrganisation.id])}">
										<span><g:dateFormat format="yyyy" date="${period.startDate}"/></span>
									</a>
								</li>
							</g:each>
						</ul>
					</div> 
				</div>
			</div>
		
			<div id="top" class="box margin-bottom-10">
	    		<span class="bold"><g:message code="dashboard.labels.objectives" default="Objectives"/></span>
		    	<ul class="inline-list">
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
	    	
	    	<div id="bottom">
	    		<div id="left">
			    	<div class="box margin-bottom-10">
			    		<div class="bold"><g:message code="dashboard.labels.organisations" default="Organisations"/></div>
				    	<ul>
				    		<g:each in="${dashboard.organisationPath}" var="organisation">
					    		<li>
					    			<g:link controller="dashboard" action="view" params="[period: currentPeriod.id, objective: currentObjective.id, organisation: organisation.id]">${organisation.name}</g:link>
					    		</li>
				    		</g:each>
				    		<li>
				    			${currentOrganisation.name}
				    		</li>
				    	</ul>
			    	</div>
			    	
			    	<div class="box" id="facility-type-filter">
			    		<div class="bold"><g:message code="dashboard.labels.facility" default="Facility Types"/></div>
			    		<g:if test="${!dashboard.facilityTypes.isEmpty()}">
				    		<g:each in="${dashboard.facilityTypes}" var="group">
					    		<input type="checkbox" value="${group.uuid}" ${checkedFacilities.contains(group.uuid)?'checked="checked"':'""'}/>${group.name}<br/>
				    		</g:each>
			    		</g:if>
			    		<g:else>
			    			<span class="italic"><g:message code="dashboard.labels.nofacility" default="filter not available at this level"/></span>
			    		</g:else>
			    	</div>
		    	</div>
		    
		    	<div id="center" class="box">
		    		<div id="values">
				    	<table class="nice-table">	
				    		<thead class="header">
					    		<tr class="row">
					    			<th class="cell label left">&nbsp;</th>
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
														<a href="${createLinkWithTargetURI(controller:'dashboardObjective',action:'edit', id:objectiveEntry.id)}">(<g:message code="dashboard.admin.edit" default="edit"/>)</a>
													</span>
												</g:if>
												<g:else>
													<span>
														<a href="${createLinkWithTargetURI(controller:'dashboardTarget',action:'edit', id:objectiveEntry.id)}">(<g:message code="dashboard.admin.edit" default="edit"/>)</a>
													</span>
												</g:else>
												<g:if test="${!objective.hasChildren()}">
													<g:if test="${!objective.isTarget()}">
														<span>
															<a href="${createLinkWithTargetURI(controller:'dashboardObjective',action:'delete', id:objectiveEntry.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
																(<g:message code="dashboard.admin.delete" default="delete"/>)
															</a>
														</span>
													</g:if>
													<g:else>
														<span>
															<a href="${createLinkWithTargetURI(controller:'dashboardTarget',action:'delete', id:objectiveEntry.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
																(<g:message code="dashboard.admin.delete" default="delete"/>)
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
								<tr class="row organisation" data-group="${organisation.organisationUnitGroup?.uuid}">
									<th class="cell label left row-${organisation.id}" data-row="${organisation.id}">
										<div><span>
										<g:if test="${organisation.getChildren().size() > 0}">
											<g:link controller="dashboard" action="view" params="[period: currentPeriod.id, objective: currentObjective.id, organisation: organisation.id]">${organisation.name}</g:link>
										</g:if>
										<g:else>
											${organisation.name}
										</g:else>
										</span></div>
									</th>
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
												<g:if test="${percentage.isHasMissingValue()}">
													<span><!-- missing value --></span>
												</g:if>
												<g:if test="${percentage.isHasMissingExpression()}">
													<span><!-- missing expression --></span>
												</g:if>
												</div>
											</g:if>
											<g:else>
												<div class="">&nbsp;</div>
											</g:else>
										</td>
									</g:each></tr>
									<tr>
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
								<div><a href="${createLinkWithTargetURI(controller:'dashboardTarget', action:'create', params:[currentObjective: currentObjective.id])}"><g:message code="dashboard.admin.add.target" default="Add target"/></a></div>
								<div><a href="${createLinkWithTargetURI(controller:'dashboardObjective', action:'create', params:[currentObjective: currentObjective.id])}"><g:message code="dashboard.admin.add.objective" default="Add objective"/></a></div>
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