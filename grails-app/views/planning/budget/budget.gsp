<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.budget.title" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="content" class="push">
			<div class="wrapper">
				<div class="main">
					<g:render template="/planning/planningTabs" model="[planning: planning, location: location, selected: "budget"]"/>
	        		<g:render template="/templates/help" model="[content: message(code:'planning.budget.help')]"/>
	        		
					<div id="questions">
						<div class="question push-20">
							<h4 class="section-title">
								<span class="question-default">
									<r:img uri="/images/icons/star_small.png"/>
								</span>
								<g:message code="planning.budget.budget"/>: <g:i18n field="${location.names}"/>
							</h4>
							<div class="budget">
								<p id="js_budget-warning" class="context-message warning ${planningLists.find {!it.budgetUpdated}?'':'hidden'}">
									<g:message code="planning.budget.update"/> <a href="${createLink(controller:'editPlanning', action:'updateBudget', params:[location:location.id, planning:planning.id])}"><g:message code="planning.budget.update.link"/></a>.
								</p>
								<g:if test="${!planningLists.find {!it.planningEntryBudgetList.empty}}">
									<p class="context-message warning">
										<a href="${createLink(controller:'editPlanning', action:'overview', params:[location:location.id, planning:planning.id])}"><g:message code="planning.budget.enteractivity.link"/></a> <g:message code="planning.budget.enteractivity.instructions"/>
									</p>
								</g:if>
								<g:else>
									<div class="table-wrap left clear">
										<table class="nested budget push-top-10">
											<thead>
												<tr>
													<th></th>
													<th><g:message code="planning.budget.table.incoming"/></th>
													<th><g:message code="planning.budget.table.outgoing"/></th>
													<th><g:message code="planning.budget.table.difference"/></th>
													<th class="status"></th>
												</tr>
											</thead>
											<tbody>
												<!-- 
													Each PLANNING TYPE, this should not be displayed
												    if there is no entries in the corresponding planning type 
												-->
												<g:each in="${planningLists}" var="planningTypeBudget">
													<g:if test="${!planningTypeBudget.planningEntryBudgetList.empty}">
														<g:set var="planningType" value="${planningTypeBudget.planningType}"/>
														<tr class="tree-sign js_foldable standout">
															<td class="js_foldable-toggle">
																<span>
																	<g:i18n field="${planningTypeBudget.planningType.namesPlural}"/>
																</span>
															</td>
															<td>(${planningTypeBudget.outgoing})</td>
															<td>${planningTypeBudget.incoming}</td>
															<td>${planningTypeBudget.difference}</td>
															<td class="status"></td>
														</tr>
														<tr class="sub-tree js_foldable-container hidden">
															<td colspan="7" class="bucket">
														    <table>
																	<tbody>
																		
																		<!-- 
																			Each INDIVIDUAL UNDERTAKINGS, this is always
																			displayed because we assume there's costing for each undertaking,
																			either OUTGOING or INCOMING or both
																		-->
																		<g:each in="${planningTypeBudget.planningEntryBudgetList}" var="budgetPlanningEntry">
																			<tr id="planning-${planningType.id}-${budgetPlanningEntry.lineNumber}" class="tree-sign js_foldable">
																				<td class="js_foldable-toggle">
																					<span style="margin-left: 20px;">
																						<a class="js_budget-section-link" href="${createLink(controller:'editPlanning', action:'editPlanningSection', params:[location:location.id, planningType:planningTypeBudget.planningType.id, lineNumber: budgetPlanningEntry.lineNumber, section: planningTypeBudget.planningType.sections[0]])}">
																							<g:value value="${budgetPlanningEntry.fixedHeaderValue}" type="${budgetPlanningEntry.type.fixedHeaderType}" nullText="none entered"/>
																						</a>
																					</span>
																				</td>
																				<td>(${budgetPlanningEntry.outgoing})</td>
																				<td>${budgetPlanningEntry.incoming}</td>
																				<td>${budgetPlanningEntry.difference}</td>
																				<td class="status 
																					${!budgetPlanningEntry.invalidSections.empty?'invalid':''} 
																					${!budgetPlanningEntry.incompleteSections.empty?'incomplete':''}
																					${(!budgetPlanningEntry.incompleteSections.empty || !budgetPlanningEntry.incompleteSections.empty)?'tooltip-TODO':''}
																					" title=""></td>
																			</tr>
																			<tr class="sub-tree js_foldable-container hidden">
																				<td colspan="7" class="bucket">
																					<table>
																						<tbody>
																							<g:each in="${planningType.costs}" var="planningCost">
																								<g:render template="/planning/budget/costs" model="[budgetPlanningEntry: budgetPlanningEntry, planningType: planningTypeBudget.planningType, planningCost: planningCost]"/>
																							</g:each>
																						</tbody>
																					</table>
																				</td>
																			</tr>
																		</g:each>
																	</tbody>
																</table>
															</td>
														</tr>
													</g:if>
												</g:each>
												<tr class="total">
													<td><g:message code="planning.budget.table.total"/>:</td>
													<td>${outgoing}</td>
													<td>${incoming}</td>
													<td>${difference}</td>
													<td class="status"></td>
												</tr>
											</tbody>
										</table>
										<br />
									</div>
								
								</g:else>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>