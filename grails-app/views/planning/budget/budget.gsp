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
	        		<g:render template="/templates/help" model="[content: 'Some help information for the budget']"/>
	        		
					<div id="questions">
						<div class="question push-20">
							<h4 class="section-title">
								<span class="question-default">
									<r:img uri="/images/icons/star_small.png"/>
								</span>
								Operational Undertakings: <g:i18n field="${location.names}"/>
							</h4>
							<div class="budget">
								<p id="js_budget-warning" class="context-message warning ${planningLists.find {!it.budgetUpdated}?'':'hidden'}">
									Some activities were changed. Please <a href="${createLink(controller:'editPlanning', action:'updateBudget', params:[location:location.id, planning:planning.id])}">update your budget</a>.
								</p>
								<g:if test="${!planningLists.find {!it.planningEntryBudgetList.empty}}">
									<p class="context-message warning">
										Please <a href="${createLink(controller:'editPlanning', action:'overview', params:[location:location.id, planning:planning.id])}">enter an activity</a> and come back to the budget page.
									</p>
								</g:if>
								<g:else>
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
												<g:each in="${planningLists}" var="planningTypeBudget">
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
														<td><input type="checkbox"></td>
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
																		<g:set var="planningEntry" value="${budgetPlanningEntry.planningEntry}"/>
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
																			<td><input type="checkbox"></td>
																			<td class="status 
																				${!budgetPlanningEntry.invalidSections.empty?'invalid':''} 
																				${!budgetPlanningEntry.incompleteSections.empty?'incomplete':''}
																				${(!budgetPlanningEntry.incompleteSections.empty || !budgetPlanningEntry.incompleteSections.empty)?'tooltip':''}
																				" title="Help message"></td>
																		</tr>
																		<tr class="sub-tree js_foldable-container hidden">
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
								</g:else>
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
					url: "${createLink(controller:'editPlanning', action:'saveValue', params: [location: location.id])}", 
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
					var row = $(this).parents('tr').first();
					
					rightPaneQueue.abort();
					rightPaneQueue.clear();
				
					$.manageAjax.add(queueName, {
						url: $(this).attr('href'),
						beforeSend: function() {
							$('.active-row').removeClass('active-row');
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
							row.addClass('active-row');
						},
						error: function() {
							$('#js_budget-section-edit').removeClass('loading');
							$('#js_budget-section-edit').addClass('warning');
							$('#js_budget-section-edit .js_warning-message').show();
							row.addClass('active-row');
						}
					});
					
					return false;
				});
			});
		</r:script>
	</body>
</html>