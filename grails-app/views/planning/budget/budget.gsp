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
							 	<p id="js_budget-warning" class="context-message warning ${updatedBudget?'hidden':''}">
							  	Some activities were changed. Please <a href="${createLink(controller:'planning', action:'updateBudget', params:[location:location.id, planning:planning.id])}">update your budget</a>.
							  </p>
							  <p class="context-message warning">A message that you need to <a href="#">enter an activity</a> in order to view the budget table.</p>
								<div class="table-wrap left clear">
								  
									<table class="nested budget push-top-10">
										<thead>
											<tr>
												<th></th>
												<th>Outgoing</th>
												<th>Incoming</th>
												<th>Difference</th>
												<th>General Fund</th>
												<th class="status"></th>
											</tr>
										</thead>
										<tbody>
											<!-- 
												Each PLANNING TYPE, this should not be displayed
											    if there is no entries in the corresponding planning type 
											-->
											<g:each in="${planningTypeBudgets}" var="planningTypeBudget">
												<g:set var="planningType" value="${planningTypeBudget.planningType}"/>
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
													<td class="status"></td>
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
																	<g:set var="planningEntry" value="${budgetPlanningEntry.planningEntry}"/>
																	<tr id="planning-${planningType.id}-${planningEntry.lineNumber}" class="tree_sign_minus active-row">
																		<td>
																			<span style="margin-left: 20px;">
																				<a class="js_budget-section-link" href="${createLink(controller:'planning', action:'editPlanningSection', params:[location:location.id, planningType:planningTypeBudget.planningType.id, lineNumber: budgetPlanningEntry.planningEntry.lineNumber, section: planningTypeBudget.planningType.sections[0]])}">
																					<g:value value="${planningEntry.discriminatorValue}" type="${planningTypeBudget.planningType.discriminatorType}" enums="${budgetPlanningEntry.planningEntry.enums}"/>
																				</a>
																			</span>
																		</td>
																		<td>(${budgetPlanningEntry.outgoing})</td>
																		<td>${budgetPlanningEntry.incoming}</td>
																		<td>${budgetPlanningEntry.difference}</td>
																		<td><input type="checkbox"></td>
																		<td class="status 
																			${!planningEntry.invalidSections.empty?'invalid':''} 
																			${!planningEntry.incompleteSections.empty?'incomplete':''}
																			${(!planningEntry.incompleteSections.empty || !planningEntry.incompleteSections.empty)?'tooltip':''}
																			" title="Help message"></td>
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
												<td class="status"></td>
											</tr>
										</tbody>
									</table>
									<br />
									<input type="submit" value="Submit">
								</div>
								
								<div class="right table-aside">
									<p class="context-message success">TODO Budget difference: 70 Million RWD</p>
									<div class="diff context-message hidden" id="js_budget-section-edit">
										<div class="js_content">
										
										</div>
										<span class="hidden js_warning-message">
											Could not load panel
										</span>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<r:script>
			$(document).ready(function() {
				${render(template:'/templates/messages')}

				var dataEntry = new DataEntry({
					element: $('#js_budget-section-edit'),
					callback: function(dataEntry, data, element) {
						if (data.complete) $(escape('#planning-'+data.id+'-'+data.lineNumber)).find('.status').removeClass('incomplete')
						else $(escape('#planning-'+data.id+'-'+data.lineNumber)).find('.status').addClass('incomplete')
						if (data.valid) $(escape('#planning-'+data.id+'-'+data.lineNumber)).find('.status').removeClass('invalid')
						else $(escape('#planning-'+data.id+'-'+data.lineNumber)).find('.status').addClass('invalid')
						
						if ($(escape('#planning-'+data.id+'-'+data.lineNumber)).find('.status').hasClass('incomplete')) $(escape('#planning-'+data.id+'-'+data.lineNumber)).find('.status').addClass('tooltip');
						if ($(escape('#planning-'+data.id+'-'+data.lineNumber)).find('.status').hasClass('invalid')) $(escape('#planning-'+data.id+'-'+data.lineNumber)).find('.status').addClass('tooltip');
						
						if (data.budgetUpdated) $('#js_budget-warning').hide();
						else $('#js_budget-warning').show();
					},
					url: "${createLink(controller:'planning', action:'saveValue', params: [location: location.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});

				var queueName = 'queue'+Math.floor(Math.random()*11);
				var rightPaneQueue = $.manageAjax.create(queueName, {
					type : 'POST',
					dataType: 'json',
					queue: 'clear',
					cacheResponse: false,
					maxRequests: 1,
					abortOld: true
				});			
		
				$('.js_budget-section-link').bind('click', function(){
					rightPaneQueue.abort();
					rightPaneQueue.clear();
				
					$.manageAjax.add(queueName, {
						url: $(this).attr('href'),
						beforeSend: function() {
							$('#js_budget-section-edit').show();
							$('#js_budget-section-edit').removeClass('warning');
							$('#js_budget-section-edit .js_warning-message').hide();
							$('#js_budget-section-edit').addClass('loading');
							$('#js_budget-section-edit .js_content').html('');
						},
						success: function(data) {
							if (data.status == 'success') {
								$('#js_budget-section-edit .js_content').html(data.html);
								$('#js_budget-section-edit').removeClass('loading');
								$('#js_budget-section-edit').removeClass('warning');
								$('#js_budget-section-edit .js_warning-message').hide();
								
								dataEntry.enableAfterLoading();
							}
							else {
								$('#js_budget-section-edit').removeClass('loading');
								$('#js_budget-section-edit').addClass('warning');
								$('#js_budget-section-edit .js_warning-message').show();
							}
						},
						error: function() {
							$('#js_budget-section-edit').removeClass('loading');
							$('#js_budget-section-edit').addClass('warning');
							$('#js_budget-section-edit .js_warning-message').show();
						}
					});
					
					return false;
				});
			});
		</r:script>
	</body>
</html>