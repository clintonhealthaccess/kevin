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
	        		<g:render template="/templates/help" model="[content: i18n(field: planning.budgetHelps)]"/>
	        		
					<div id="questions">
						<div class="question push-20">
							<h4 class="section-title">
								<span class="question-default">
									<r:img uri="/images/icons/star_small.png"/>
								</span>
								<g:message code="planning.budget.budget"/>: <g:i18n field="${location.names}"/>
							</h4>
							<div class="budget">
								<p id="js_budget-warning" class="context-message warning ${budgetNeedsUpdate?'':'hidden'}">
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
															<td><g:formatNumber number="${planningTypeBudget.incoming}" format="#,###"/></td>
															<td><g:formatNumber number="${planningTypeBudget.outgoing}" format="#,###"/></td>
															<td><g:formatNumber number="${planningTypeBudget.difference}" format="#,###"/></td>
															<td class="status"></td>
														</tr>
														<tr class="sub-tree js_foldable-container hidden">
															<g:if test="${planningType.maxNumber != 1}">
																<td colspan="7" class="bucket">
															    	<table>
																		<tbody>
																			<!-- 
																				Each INDIVIDUAL UNDERTAKINGS, this is always
																				displayed because we assume there's costing for each undertaking,
																				either OUTGOING or INCOMING or both
																			-->
																			<g:each in="${planningTypeBudget.planningEntryBudgetList}" var="budgetPlanningEntry">
																				<tr id="planning-${planningType.id}-${budgetPlanningEntry.lineNumber}" class="tree-sign js_foldable budget-entry">
																					<td>
																						<span class="js_foldable-toggle" style="margin-left: 20px;"> <a href="#">&zwnj;</a> </span>
																						<span>
																							<a class="js_budget-section-link" href="${createLink(controller:'editPlanning', action:'editPlanningEntry', params:[location:location.id, planningType:planningTypeBudget.planningType.id, lineNumber: budgetPlanningEntry.lineNumber])}">
																								<g:if test="${planningType.fixedHeader != null && !planningType.fixedHeader.empty}">
																									<g:value value="${budgetPlanningEntry.fixedHeaderValue}" type="${budgetPlanningEntry.type.fixedHeaderType}" nullText="none entered"/>
																								</g:if>
																								<g:else>
																									<g:i18n field="${planningType.names}"/> ${budgetPlanningEntry.lineNumber + 1}
																								</g:else>
																							</a>
																						</span>
																					</td>
																					<td><g:formatNumber number="${budgetPlanningEntry.incoming}" format="#,###"/></td>
																					<td><g:formatNumber number="${budgetPlanningEntry.outgoing}" format="#,###"/></td>
																					<td><g:formatNumber number="${budgetPlanningEntry.difference}" format="#,###"/></td>
																					<td class="status ${!budgetPlanningEntry.invalidSections.empty?'invalid':!budgetPlanningEntry.incompleteSections.empty?'incomplete':'complete'}" title=""></td>
																				</tr>
																				<tr class="sub-tree js_foldable-container hidden">
																					<td colspan="7" class="bucket">
																						<g:render template="/planning/budget/costs" model="[budgetPlanningEntry: budgetPlanningEntry, planningType: planningTypeBudget.planningType, margin: 40]"/>
																					</td>
																				</tr>
																			</g:each>
																		</tbody>
																	</table>
																</td>
															</g:if>
															<g:else>
																<g:each in="${planningTypeBudget.planningEntryBudgetList}" var="budgetPlanningEntry">
																	<td colspan="7" class="bucket">
																		<g:render template="/planning/budget/costs" model="[budgetPlanningEntry: budgetPlanningEntry, planningType: planningTypeBudget.planningType, margin: 20]"/>
																	</td>
																</g:each>
															</g:else>
														</tr>
													</g:if>
												</g:each>
												<tr class="total">
													<td><g:message code="planning.budget.table.total"/>:</td>
													<td><g:formatNumber number="${incoming}" format="#,###"/></td>
													<td><g:formatNumber number="${outgoing}" format="#,###"/></td>
													<td class="${difference < 0?'red':''}"><g:formatNumber number="${difference}" format="#,###"/></td>
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