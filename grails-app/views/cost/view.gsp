<%@ page import="org.chai.kevin.cost.CostTarget.CostType" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="cost.view.label" default="Costing" /></title>
    </head>
    <body>
    	<div id="cost">
			<div id="top" class="box">
				<div class="filter">
					<h5>Iteration</h5>
					<div class="dropdown">
						<a href="#" class="selected"><g:dateFormat format="yyyy" date="${costTable.currentPeriod.startDate}"/></a>
						<div class="hidden dropdown-list">
							<ul>
								<g:each in="${periods}" var="period">
									<li>
										<a href="${createLink(controller: "cost", action:"view", params:[period: period.id, objective: costTable.currentObjectiveId, organisation: costTable.currentOrganisationId])}">
											<span><g:dateFormat format="yyyy" date="${period.startDate}"/></span>
										</a>
									</li>
								</g:each>
							</ul>
						</div> 
					</div>
				</div>
				<div class="filter">
					<h5>Organisation:</h5>
					<div class="dropdown">
						<g:if test="${costTable.currentOrganisation != null}">
							<a href="#" class="selected">${costTable.currentOrganisation.name}</a>
						</g:if>
						<g:else>
							<a href="#" class="selected">no organisation selected</a>
						</g:else>
						<div class="hidden dropdown-list">
							<ul>
								<g:render template="/templates/organisationTree" model="[controller: 'cost', action: 'view', organisation: organisationTree, params:[period: costTable.currentPeriod.id, objective: costTable.currentObjectiveId], displayLinkUntil: displayLinkUntil]"/>
							</ul>
						</div>
					</div>
				</div>
				<div class="filter">
					<h5>Strategic Objective:</h5>
					<div class="dropdown">
						<g:if test="${costTable.currentObjective != null}">
							<a href="#" class="selected"><g:i18n field="${costTable.currentObjective.names}"/></a>
						</g:if>
						<g:else>
							<a href="#" class="selected">no objective selected</a>
						</g:else>
						<div class="hidden dropdown-list">
							<g:if test="${!objectives.empty}">
								<ul>
									<g:each in="${objectives}" var="objective">
										<li>
											<span>
												<a href="${createLink(controller: "cost", action:"view", params:[period: costTable.currentPeriod.id, objective: objective.id, organisation: costTable.currentOrganisationId])}">
													<g:i18n field="${objective.names}"/>
												</a>
											</span>
											<span>
												<g:link controller="costObjective" action="edit" id="${objective.id}" class="flow-edit">(edit)</g:link>
											</span>
										</li>
									</g:each>
								</ul>
							</g:if>
							<g:else>
								<span>no objectives found</span>
							</g:else>
						</div>
					</div>
				</div>
				<g:if test="${true || user.admin}">
					<div>
						<a class="flow-add" id="add-cost-objective-link" href="${createLink(controller:'costObjective', action:'create')}">add objective</a>
					</div>
				</g:if>
				
			</div>
    		<div id="center" class="box">
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
												<span>
													<a href=${createLink(controller:"costTarget", action:"edit", id: target.id)} class="flow-edit">(edit)</a>
												</span>
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
						<g:if test="${true || user.admin}">
							<div>
								<a id="add-cost-target-link" class="flow-add" href="${createLink(controller:'costTarget', action:'create', params:[currentObjective: costTable.currentObjective?.id])}">add target</a>
							</div>
						</g:if>
						<!-- ADMIN SECTION END -->
					</g:if>
					<g:else>
						<div>Please select an organisation / objective</div>
					</g:else>
				</div>
				<!-- ADMIN SECTION -->
		    	<g:if test="${true || user.admin}">
	    			<div class="hidden flow-container"></div>

					<script type="text/javascript">
						$(document).ready(function() {
							$('#values').flow({
								onSuccess: function(data) {
									if (data.result == 'success') {
										location.reload();
									}
								}
							});
						});
					</script>
		    	</g:if>
		    	<!-- ADMIN SECTION END -->
				<div class="clear"></div>
    		</div>
    	</div>
    	
    	<script type="text/javascript">
    		$(document).ready(function(){
    			$('.cell.label').bind('click', function() {
    				var target = $(this).data('row');
    				explanationClick(this, 'target-'+target, function(){});
    				return false;
    			});
    		});
    	</script>
    </body>
</html>