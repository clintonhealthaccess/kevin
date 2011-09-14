<%@ page import="java.text.SimpleDateFormat" %>
<% SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy"); %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="dashboard.view.label" default="Dashboard" /></title>
    </head>
    <body>
		<div id="dashboard">
			<div id="corner" class="box">
				<h5><g:message code="dashboard.labels.iteration" default="Iteration"/></h5>
				<div class="dropdown white-dropdown">
					<a class="selected" href="#"><g:dateFormat format="yyyy" date="${dashboard.currentPeriod.startDate}"/></a>
					<div class="hidden dropdown-list">
						<ul>
							<g:each in="${periods}" var="period">
								<li>
									<a href="${createLink(controller: "dashboard", action:"view", params:[period: period.id, objective: dashboard.currentObjective.id, organisation: dashboard.currentOrganisation.id])}">
										<span><g:dateFormat format="yyyy" date="${period.startDate}"/></span>
									</a>
								</li>
							</g:each>
						</ul>
					</div> 
				</div>
			</div>
		
			<div id="top" class="box">
	    		<h5 class="float"><g:message code="dashboard.labels.objectives" default="Objectives"/></h5>
		    	<ul>
		    		<g:each in="${dashboard.objectivePath}" var="objective">
			    		<li>
			    			<g:link controller="dashboard" action="view" params="[period: dashboard.currentPeriod.id, objective: objective.id, organisation: dashboard.currentOrganisation.id]"><g:i18n field="${objective.names}"/></g:link>
			    		</li>
		    		</g:each>
		    		<li>
		    			<g:i18n field="${dashboard.currentObjective.names}"/>
		    		</li>
		    	</ul>
	    	</div>
	    	
	    	<div id="bottom">
	    		<div id="left">
			    	<div class="box">
			    		<h5><g:message code="dashboard.labels.organisations" default="Organisations"/></h5>
				    	<ul>
				    		<g:each in="${dashboard.organisationPath}" var="organisation">
					    		<li>
					    			<g:link controller="dashboard" action="view" params="[period: dashboard.currentPeriod.id, objective: dashboard.currentObjective.id, organisation: organisation.id]">${organisation.name}</g:link>
					    		</li>
				    		</g:each>
				    		<li>
				    			${dashboard.currentOrganisation.name}
				    		</li>
				    	</ul>
			    	</div>
			    	
			    	<div class="box" id="facility-type-filter">
			    		<h5><g:message code="dashboard.labels.facility" default="Facility Types"/></h5>
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
													href="${createLink(controller:'dashboard', action:'view', params:[period: dashboard.currentPeriod.id, objective: objective.id, organisation: dashboard.currentOrganisation.id])}"
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
														<a class="flow-edit" href="${createLink(controller:'dashboardObjective',action:'edit',id:objectiveEntry.id)}">(<g:message code="dashboard.admin.edit" default="edit"/>)</a>
													</span>
												</g:if>
												<g:else>
													<span>
														<a class="flow-edit" href="${createLink(controller:'dashboardTarget',action:'edit',id:objectiveEntry.id)}">(<g:message code="dashboard.admin.edit" default="edit"/>)</a>
													</span>
												</g:else>
												<g:if test="${!objective.hasChildren()}">
													<g:if test="${!objective.isTarget()}">
														<span>
															<a class="flow-delete" href="${createLink(controller:'dashboardObjective',action:'delete',id:objectiveEntry.id)}">(<g:message code="dashboard.admin.delete" default="delete"/>)</a>
														</span>
													</g:if>
													<g:else>
														<span>
															<a class="flow-delete" href="${createLink(controller:'dashboardTarget',action:'delete',id:objectiveEntry.id)}">(<g:message code="dashboard.admin.delete" default="delete"/>)</a>
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
											<g:link controller="dashboard" action="view" params="[period: dashboard.currentPeriod.id, objective: dashboard.currentObjective.id, organisation: organisation.id]">${organisation.name}</g:link>
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
												    	<a class="no-link" href="${createLink(controller:'dashboard', action:'explain', params:[objective: objective.id, organisation: organisation.id, period: dashboard.currentPeriod.id])}">
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
								<div><a id="add-dashboard-target-link" class="flow-add" href="${createLink(controller:'dashboardTarget', action:'create', params:[currentObjective: dashboard.currentObjective.id])}"><g:message code="dashboard.admin.add.target" default="Add indicator"/></a></div>
								<div><a id="add-dashboard-objective-link" class="flow-add" href="${createLink(controller:'dashboardObjective', action:'create', params:[currentObjective: dashboard.currentObjective.id])}"><g:message code="dashboard.admin.add.objective" default="Add objective"/></a></div>
							</div>
						</shiro:hasPermission>
				    	<!-- ADMIN SECTION END -->
				    	
			    	</div>
			    	<!-- ADMIN SECTION -->
		    		<shiro:hasPermission permission="admin:dashboard">
		    			<div class="hidden flow-container"></div>
						
						<script type="text/javascript">
							$(document).ready(function() {
								$('#values').flow({
									onSuccess: function(data){
										if (data.result == 'success') {
											location.reload();
										}
									}
								});
							});
						</script>
			    	</shiro:hasPermission>
			    	<!-- ADMIN SECTION END -->
			    </div>
			    <!-- center -->
	    	</div>
	    </div>
	
    	<!-- dashboard specific functionality -->
    	<script type="text/javascript">
    		$(document).ready(function() {
    		
    			/**
    			 * cluetip
    			 **/
    			$('a.cluetip').cluetip(cluetipOptions);
    			 
    			/**
    			 * dashboard
    			 **/
    			$('.cell.value').bind('click', function() {
    				var organisation = $(this).data('row');
    				var objective = $(this).data('col');
    				explanationClick(this, organisation+'-'+objective, function(){});
    				return false;
    			});
    			
    			/**
    			 * facility type switcher
    			 **/
    			$('#facility-type-filter input').bind('click', function() {
    				toggleFacilityType();
    			});
				
    			toggleFacilityType();
    		});

    		function addEvents(prefix) {
    			$('#explanation-'+prefix+' .element').each(function(){
	    			var id = $(this).data('id');
    				var organisation = $(this).data('organisation');
    				var objective = $(this).data('objective');
    				var elementId = '#data-'+organisation+'-'+objective+'-'+id;
    			
    				$(this).bind('mouseenter mouseleave', function() {
	    				$(elementId).toggleClass('highlighted');
	    				$(this).toggleClass('highlighted');
	    			});
    				$(this).bind('mouseenter', function() {
    					$(elementId).find('a.cluetip').mouseover();    					
    				});
    				$(this).bind('mouseleave', function() {
    					$(elementId).find('a.cluetip').mouseleave();
    				});
	    			$(this).bind('click', function() {
	    				if (!$(this).hasClass('selected')) {
							$('.element').removeClass('selected');
		   					$('.data').removeClass('selected');
		    			}
	   					$(elementId).toggleClass('selected');
		   				$(this).toggleClass('selected');
		   			});
    			});
    			
    			$('a.cluetip').cluetip(cluetipOptions);
    		}
    	</script>
    	<!-- explanation -->
    </body>
</html>