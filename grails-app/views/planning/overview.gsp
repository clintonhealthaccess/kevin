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
				<div id="report">
					<div class="main">
					
						<g:render template="/planning/planningTabs" model="[planning: planning, location: location, selected: 'undertakings']"/>
						<g:render template="/planning/undertakingsTabs" model="[planning: planning, location: location, selected: 'overview']"/>
				    
				    	<!-- TODO tips could go into a template -->
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
		        		<ul class="overview-section">
		        			<g:each in="${planningLists}" var="planningList">
		        				<li>
		        					<h5 class="${!planningList.empty?'left':''}">
		        						<a href="${createLink(controller:'planning', action:'planningList', params:[planningType:planningList.planningType.id, location:location.id])}">
		        							<g:i18n field="${planningList.planningType.namesPlural}"/>
		        						</a>
		        					</h5>
		        					
		        					<g:if test="${!planningList.empty}">
			        					<p class="right">
			        						<a class="overview-all" href="${createLink(controller:'planning', action:'planningList', params:[planningType:planningList.planningType.id, location:location.id])}">
			        							View All <span>${planningList.planningEntries.size()}</span> <g:i18n field="${planningList.planningType.namesPlural}"/>
			        						</a>
			        					</p>
			        				
			        					<h6>Recently Added</h6>
			        					<ul class="overview-recent">
			        						<g:each in="${planningList.getLatestEntries(5)}" var="entry">
												<li>
													<a href="${createLink(controller:'planning', action:'editPlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:entry.lineNumber])}">
														<g:value value="${entry.discriminatorValue}" type="${planningList.planningType.discriminatorType}" enums="${entry.enums}"/>
													</a>
													<span class="overview-manage right">
														<a href="${createLink(controller:'planning', action:'editPlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:entry.lineNumber])}">edit</a>
														<a href="${createLinkWithTargetURI(controller:'planning', action:'deletePlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:entry.lineNumber])}">delete</a>
													</span>
												</li>
											</g:each>
										</ul>
		        					</g:if>
		        					<g:else>
		        						<p>You haven't added any <g:i18n field="${planningList.planningType.namesPlural}"/> yet. 
			        						<a href="${createLink(controller:'planning', action:'editPlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:planningList.nextLineNumber])}">
			        							Add your first <g:i18n field="${planningList.planningType.names}"/>
			        						</a>
		        						</p>
		        					</g:else>
		        					<p class="overview-new">
		        						<a class="next gray medium" href="${createLink(controller:'planning', action:'editPlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:planningList.nextLineNumber])}">
		        							Create New <g:i18n field="${planningList.planningType.names}"/>
		        						</a>
		        					</p>
		        				</li>
		        			</g:each>
		        		</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>