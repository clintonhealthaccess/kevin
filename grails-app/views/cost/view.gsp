<%@ page import="org.chai.kevin.cost.CostTarget.CostType" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="cost.view.label" default="Costing" /></title>
        
        <!-- for admin forms -->
        <shiro:hasPermission permission="cost:admin">
        	<r:require modules="form,cluetip"/>
        </shiro:hasPermission>
        
        <r:require modules="cost"/>
    </head>
    <body>
    	<div id="cost">
			<div class="subnav">
				<div class="filter">
					<span class="bold"><g:message code="costing.labels.iteration" default="Iteration"/></span>
					<span class="dropdown subnav-dropdown">
						<a href="#" class="selected"><g:dateFormat format="yyyy" date="${costTable.currentPeriod.startDate}"/></a>
						<div class="hidden dropdown-list">
							<ul>
								<g:each in="${periods}" var="period">
									<li>
										<a href="${createLink(controller:'cost', action:'view', params:[period: period.id, objective: costTable.currentObjectiveId, organisation: costTable.currentOrganisationId])}">
											<span><g:dateFormat format="yyyy" date="${period.startDate}"/></span>
										</a>
									</li>
								</g:each>
							</ul>
						</div> 
					</span>
				</div>
				<div class="filter">
					<span class="bold"><g:message code="costing.labels.organisation" default="Organisation"/></span>
					<span class="dropdown subnav-dropdown">
						<g:if test="${costTable.currentOrganisation != null}">
							<a href="#" class="selected">${costTable.currentOrganisation.name}</a>
						</g:if>
						<g:else>
							<a href="#" class="selected"><g:message code="costing.labels.noorganisation" default="no organisation selected"/></a>
						</g:else>
						<div class="hidden dropdown-list">
							<ul>
								<g:render template="/templates/organisationTree" model="[controller: 'cost', action: 'view',organisation: organisationTree,current: costTable.currentOrganisation, params:[period: costTable.currentPeriod.id, objective: costTable.currentObjectiveId], displayLinkUntil: displayLinkUntil]"/>
							</ul>
						</div>
					</span>
				</div>
				<div class="filter">
					<span class="bold"><g:message code="costing.labels.objective" default="Strategic Objective"/></span>
					<span class="dropdown subnav-dropdown">
						<g:if test="${costTable.currentObjective != null}">
							<a href="#" class="selected"><g:i18n field="${costTable.currentObjective.names}"/></a>
						</g:if>
						<g:else>
							<a href="#" class="selected"><g:message code="costing.labels.noobjective" default="no objective selected"/></a>
						</g:else>
						<div class="hidden dropdown-list">
							<g:if test="${!objectives.empty}">
								<ul>
									<g:each in="${objectives}" var="objective">
										<li>
											<span>
												<a href="${createLink(controller:'cost', action:'view', params:[period: costTable.currentPeriod.id, objective: objective.id, organisation: costTable.currentOrganisationId])}">
													<g:i18n field="${objective.names}"/>
												</a>
											</span>
											<shiro:hasPermission permission="cost:admin">
												<span>
													<a href="${createLinkWithTargetURI(controller:'costObjective', action:'edit', id:objective.id)}">(Edit)</a>
												</span>
												<span>
													<a href="${createLinkWithTargetURI(controller:'costObjective', action:'delete', id:objective.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
														(Delete)
													</a>
												</span>
											</shiro:hasPermission>
										</li>
									</g:each>
								</ul>
							</g:if>
							<g:else>
								<span><g:message code="costing.labels.noobjectivesfound" default="no objectives found"/></span>
							</g:else>
						</div>
					</span>
				</div>
				<div class="clear"></div>
				<shiro:hasPermission permission="admin:cost">					
				<div>
						<a href="${createLinkWithTargetURI(controller:'costObjective', action:'create')}"><g:message code="costing.admin.add.objective" default="Add objective"/></a>
					</div>
				</shiro:hasPermission>
				
			</div>
    		<div id="center" class="main">
    			<div id="values">
    				<g:if test="${costTable.currentObjective != null && costTable.currentOrganisation != null}">
						<table class="nice-table">
							<thead>
								<tr>
									<th class="empty">&nbsp;</th>
									<g:each in="${costTable.years}" var="year">
										<th class="cell label col-${year}" data-col="${year}">${year}</th>
									</g:each>
								</tr>
							</thead>
							<tbody>
								<g:each in="${CostType.values()}" var="costType">
									<tr>
										<th class="header" colspan="${costTable.years.size() + 1}">${costType.name}</th>
									</tr>
									<g:if test="${costTable.getTargetsOfType(costType).empty}">
										<tr class="italic"><th colspan="${costTable.years.size() + 1}"><span>no target defined for this type</span></th></tr>
									</g:if>
									<g:each in="${costTable.getTargetsOfType(costType)}" var="target">
										<tr>
											<th class="cell label row-${target.id}" data-row="${target.id}">
												<span>
													<a class="no-link" href="${createLink(controller:'cost', action:'explain', params:[objective: target.id, organisation: costTable.currentOrganisationId])}"><g:i18n field="${target.names}"/></a>
												</span>
												
												<shiro:hasPermission permission="admin:cost">		
													<span>
														<a href="${createLinkWithTargetURI(controller:'costTarget', action:'edit', id: target.id)}">(edit)</a>
													</span>
												</shiro:hasPermission>
											</th>
											<g:each in="${costTable.years}" var="year">
												<g:set value="${costTable.getCost(target, year)}" var="cost"/>
												<td class="cell value row-${target.id} col-${year}" data-col="${year}" data-row="${target.id}">
													${cost.roundedValue}
												</td>
											</g:each>
										</tr>
										<tr class="explanation-row">
											<th colspan="${costTable.years.size()+1}">
												<div class="explanation-cell" id="explanation-target-${target.id}"></div>
											</th>
										</tr>
									</g:each>
								</g:each>
							</tbody>
						</table>
					
						<!-- ADMIN SECTION -->
						<shiro:hasPermission permission="admin:cost">							
							<div>
								<a href="${createLinkWithTargetURI(controller:'costTarget', action:'create', params:[currentObjective: costTable.currentObjective?.id])}">add target</a>
							</div>
						</shiro:hasPermission>
						<!-- ADMIN SECTION END -->
					</g:if>
					<g:else>
						<p class="help">Please select an organisation / objective</p>
					</g:else>
				</div>
				<div class="clear"></div>
    		</div>
    	</div>
    	
    	<r:script>
    		$(document).ready(function(){
    			$('.cell.label').bind('click', function() {
    				var target = $(this).data('row');
    				explanationClick(this, 'target-'+target, function(){});
    				return false;
    			});
    		});
    	</r:script>
    </body>
</html>