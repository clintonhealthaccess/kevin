<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.label" default="District Health System Portal" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="content" class="push">
			<div class="wrapper">
				<div class="main">
					<g:render template="/planning/planningTabs" model="[planning: planning, location: location, selected: "budget"]"/>
	        
					<p class="show-question-help moved"><a href="#">Show Tips</a></p>
					<div class="question-help-container">
						<div class="question-help push-20">
							<a class="hide-question-help" href="#">Close tips</a>Some help information for the Performance tab
						</div>
					</div>
	        
					<div id="questions">
						<div class="question push-20">
							<h4 class="section-title">
								<span class="question-default">
									<r:img uri="/images/icons/star_small.png"/>
								</span>
								Operational Undertakings: <g:i18n field="${location.names}"/>
							</h4>
							<div class="budget">
								<div class="table-wrap left clear">
	              
									<table class="nested col5 push-top-10">
										<thead>
											<tr>
												<th></th>
												<th>Outgoing</th>
												<th>Incoming</th>
												<th>Difference</th>
												<th>General Fund</th>
											</tr>
										</thead>
										<tbody>
											<!-- 
												Each PLANNING TYPE, this should not be displayed
											    if there is no entries in the corresponding planning type 
											-->
											<g:each in="${planningTypeBudgets}" var="planningTypeBudget">
												<tr class="tree_sign_minus standout">
													<td>
														<span>
															<g:i18n field="${planningTypeBudget.planningType.namesPlural}"/>
														</span>
													</td>
													<td>(${planningTypeBudget.outgoing})</td>
													<td>${planningTypeBudget.incoming}</td>
													<td>${planningTypeBudget.difference}</td>
													<td><input type="checkbox"></td>
												</tr>
												<tr style="display: table-row" class="sub_tree">
													<td colspan="7" class="bucket">
												        <table>
															<tbody>
																
																<!-- 
																	Each INDIVIDUAL UNDERTAKINGS, this is always
																	displayed because we assume there's costing for each undertaking,
																	either OUTGOING or INCOMING or both
																-->
																<g:each in="${planningTypeBudget.budgetPlanningEntries}" var="budgetPlanningEntry">
																	<tr class="tree_sign_minus active-row">
																		<td>
																			<span style="margin-left: 20px;">
																				<g:value value="${budgetPlanningEntry.planningEntry.discriminatorValue}" type="${planningTypeBudget.planningType.discriminatorType}" enums="${budgetPlanningEntry.planningEntry.enums}"/>
																			</span>
																		</td>
																		<td>(${budgetPlanningEntry.outgoing})</td>
																		<td>${budgetPlanningEntry.incoming}</td>
																		<td>${budgetPlanningEntry.difference}</td>
																		<td><input type="checkbox"></td>
																	</tr>
																	<tr style="display: table-row" class="sub_tree">
																		<td colspan="7" class="bucket">
																			<table>
																				<tbody>
																					<!--
																						OUTGOING costing formulas, only displayed if not empty
																					-->
																					<g:render template="/planning/budget/costs" model="[budgetPlanningEntry: budgetPlanningEntry, planningType: planningTypeBudget.planningType, costType: PlanningCostType.OUTGOING]"/>
																					<!--
																						INCOMING costing formulas, only displayed if not empty
																					-->
																					<g:render template="/planning/budget/costs" model="[budgetPlanningEntry: budgetPlanningEntry, planningType: planningTypeBudget.planningType, costType: PlanningCostType.INCOMING]"/>
																					
																				</tbody>
																			</table>
																		</td>
																	</tr>
																</g:each>
															</tbody>
														</table>
													</td>
												</tr>
											</g:each>
											<tr class="total">
												<td>Total:</td>
												<td>${outgoing}</td>
												<td>${incoming}</td>
												<td>${difference}</td>
												<td></td>
											</tr>
										</tbody>
									</table>
									<br />
									<input type="submit" value="Submit">
								</div>
								
								<div class="right table-aside">
									<p class="diff positive">TODO Budget difference: 70 Million RWD</p>
										<div class="diff">
											<div>
												<div class="diff-title">
													<h5>Provide Condoms</h5>
													<h6>Staffing Requirements</h6>
												</div>
												<ul>
													<li>
														<h6>Nurses</h6>
														<p><span>Minutes per instance</span><input type="text" class="idle-field"></p>
														<p><span>Level</span><select><option>A0</option></select></p>
													</li>
													<li>
														<h6>Doctors</h6>
														<p><span>Minutes per instance</span><input type="text" class="idle-field"></p>
														<p><span>Level</span><select><option>Hematologist</option></select></p>
													</li>
													<li>
														<h6>Other</h6>
														<p><span>Minutes per instance</span><input type="text" class="idle-field"></p>
														<p><span>Level</span><select><option>Nutritionist</option></select></p>
													</li>
													<li><input class="medium right" type="submit" value="Update"></li>
												</ul>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>