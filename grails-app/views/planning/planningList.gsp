<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.list.title" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="content" class="push">
			<div class="wrapper">
				<div id="report">

					<div class="main">	
						<g:render template="/planning/planningTabs" model="[planning: planning, location: location, selected: 'undertakings']"/>
						<g:render template="/planning/undertakingsTabs" model="[planning: planning, location: location, selected: planningType.id]"/>
						
        				<g:render template="/templates/help" model="[content: message(code:'planning.list.help')]"/>
        
						<ul class="clearfix" id="questions">
							<li class="question push-20">
								<g:if test="${planningList.planningType.maxNumber == null || planningList.nextLineNumber < planningList.planningType.maxNumber}">
									<a href="${createLinkWithTargetURI(controller:'editPlanning', action:'editPlanningEntry', params:[planningType: planningType.id, location: location.id, lineNumber: planningList.nextLineNumber])}" class="next medium gray right">
										<g:message code="planning.createnew" args="[i18n(field: planningType.names)]"/>
									</a>
								</g:if>
								<h4 class="section-title">
									<span class="question-default">
										<r:img uri="/images/icons/star_small.png"/>
									</span>
									<g:message code="planning.list.manage" args="[i18n(field: planningType.namesPlural)]"/>
								</h4>
								
								<g:if test="${!planningList.empty}">
									<div class="table-wrap">
									
										<table class="listing">
										
											<thead>
												<tr>
													<th></th>
													<th></th>
													<th><g:i18n field="${planningType.names}"/></th>
													<g:each in="${planningType.getValuePrefixes(section)}" var="prefix">
														<th><g:i18n field="${planningType.formElement.headers[prefix]}"/></th>
													</g:each>
													<th></th>
												</tr>
											</thead>
											
											<tbody>
												<g:each in="${planningList.planningEntries}" var="entry">
													<tr>
														<td class="status ${!entry.invalidSections.empty?'invalid':!entry.incompleteSections.empty?'incomplete':'complete'}"></td>
														<td>
															<a href="${createLinkWithTargetURI(controller:'editPlanning', action:'editPlanningEntry', params:[location:location.id, planningType:planningType.id, lineNumber:entry.lineNumber])}">
																<g:if test="${planningType.fixedHeader != null && !planningType.fixedHeader.empty}">
																	<g:value value="${entry.fixedHeaderValue}" type="${entry.type.fixedHeaderType}" nullText="none entered"/>
																</g:if>
																<g:else>
																	<g:i18n field="${planningType.names}"/>
																	<g:if test="${planningType.maxNumber != 1}">
																		${entry.lineNumber + 1}
																	</g:if>
																</g:else>
															</a>
														</td>
														<td>
															<g:value value="${entry.discriminatorValue}" type="${planningType.discriminatorType}" enums="${entry.enums}" nullText="none entered"/>
														</td>
														<g:each in="${planningType.getValuePrefixes(section)}" var="prefix">
															<td>
																<g:value value="${entry.getValue(prefix)}" type="${planningType.getType(prefix)}" enums="${entry.enums}"/>
															</td>
														</g:each>
														
														<td>
															<a class="edit-link" href="${createLinkWithTargetURI(controller:'editPlanning', action:'editPlanningEntry', params:[location:location.id, planningType:planningType.id, lineNumber:entry.lineNumber])}"><g:message code="default.link.edit.label"/></a>
															<a class="delete-link" href="${createLinkWithTargetURI(controller:'editPlanning', action:'deletePlanningEntry', params:[location:location.id, planningType:planningType.id, lineNumber:entry.lineNumber])}"><g:message code="default.link.delete.label"/></a>
														</td>
													</tr>
												</g:each>
											</tbody>
										</table>
	
										<ul class="table-nav">
											<g:each in="${planningType.sections}" var="sectionIt" status="i">
												<li>
													<a class="${section==sectionIt?'selected':''}" href="${createLink(controller:'editPlanning', action:'planningList', params:[location:location.id, section:i, planningType: planningType.id])}">
														<g:i18n field="${planningType.formElement.headers[sectionIt]}"/>
													</a>
												</li>
											</g:each>
										</ul>
									
									</div>
								</g:if>
								<g:else>
									<p class="context-message"><g:message code="planning.nothingyet" args="[i18n(field: planningList.planningType.namesPlural)]"/> 
		        						<a href="${createLinkWithTargetURI(controller:'editPlanning', action:'editPlanningEntry', params:[location:location.id, planningType:planningType.id, lineNumber:planningList.nextLineNumber])}">
		        							<g:message code="planning.addfirst" args="[i18n(field: planningType.names)]"/>
		        						</a>
	        						</p>
								</g:else>
							</li>
						</ul>
        
					</div>
				</div>
			</div>
		</div>

	</body>
</html>