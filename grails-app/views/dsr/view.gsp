<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="dsrTable.view.label" default="District Summary Reports" /></title>
		
		<!-- for admin forms -->
		<shiro:hasPermission permission="admin:dsr">
        	<r:require modules="form"/>
        </shiro:hasPermission>
        
		<r:require modules="dsr"/>
	</head>
	<body>
		<div id="dsr">
			<div class="subnav">
				<g:render template="/templates/iterationFilter" model="[linkParams:[organisation: currentOrganisation?.id, objective: currentObjective?.id]]"/>
				<g:render template="/templates/organisationFilter" model="[linkParams:[period: currentPeriod.id, objective: currentObjective?.id]]"/>
				<g:render template="/templates/objectiveFilter" model="[linkParams:[period: currentPeriod.id, organisation: currentOrganisation?.id]]"/>
								
				<div class="right">
					<!-- ADMIN SECTION -->
					<shiro:hasPermission permission="admin:dsr">
						<span><a href="${createLinkWithTargetURI(controller:'dsrObjective', action:'create')}">Add Objective</a> </span>|
						<span><a href="${createLinkWithTargetURI(controller:'dsrTarget', action:'create')}">Add Target</a> </span>|
						<span><a href="${createLinkWithTargetURI(controller:'dsrTargetCategory', action:'create')}">Add Target Category</a> </span>
					</shiro:hasPermission>
					<!-- ADMIN SECTION END -->
				</div>
			</div>
			<g:if test="${dsrTable != null}">
				<g:render template="/templates/facilityTypeFilter" model="[facilityTypes: dsrTable.facilityTypes]"/>
			</g:if>
			<div id="center" class="main">
				<div id="values">
					<g:if test="${dsrTable != null}">
						<g:if test="${!dsrTable.targets.empty}">
							<table class="nice-table">
								<thead>
									<tr>
										<th class="object-name-box" rowspan="2">
											<div>
												<g:i18n field="${currentObjective.names}" />
											</div> 
											<shiro:hasPermission permission="admin:dsr">
												<span>
													<a class="edit-link" href="${createLinkWithTargetURI(controller:'dsrObjective', action:'edit', id:currentObjective.id)}">
														<g:message code="default.link.edit.label" default="Edit" />
													</a>
												</span>
												<span>
													<a class="delete-link" href="${createLinkWithTargetURI(controller:'dsrObjective', action:'delete', id:currentObjective.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
														<g:message code="default.link.delete.label" default="Delete" />
													</a>
												</span>
											</shiro:hasPermission>
										</th>
										<g:set var="i" value="${0}" />
										<g:each in="${dsrTable.targets}" var="target">
											<g:if test="${target.category != null}">
												<g:set var="i" value="${i+1}" />
												<g:if test="${i==target.category.getTargetsForObjective(currentObjective).size()}">
													<th class="title-th" colspan="${i}">
														<span>
															<g:i18n field="${target.category.names}" />
														</span> 
														<shiro:hasPermission permission="admin:dsr">
															<ul class="horizontal">
																<li>
																	<a class="edit-link" href="${createLinkWithTargetURI(controller:'dsrTargetCategory', action:'edit', id:target?.id)}">
																		<g:message code="default.link.edit.label" default="Edit" />
																	</a>
																</li>
																<li> 
																	<a class="delete-link" href="${createLinkWithTargetURI(controller:'dsrTargetCategory', action:'delete', id:target?.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
																		<g:message code="default.link.delete.label" default="Delete" />
																	</a>
																</li>
															</ul>
														</shiro:hasPermission>
													</th>
													<g:set var="i" value="${0}" />
												</g:if>
											</g:if>
											<g:else>
												<th class="title-th" rowspan="2">
													<div class="bt">
														<g:i18n field="${target.names}" />
													</div>
													<shiro:hasPermission permission="admin:dsr">
														<ul class="horizontal">
															<li>
																<a class="edit-link" href="${createLinkWithTargetURI(controller:'dsrTarget', action:'edit', id:target?.id)}">
																	<g:message code="default.link.edit.label" default="Edit" />
																</a>
															</li>
															<li> 
																<a class="delete-link" href="${createLinkWithTargetURI(controller:'dsrTarget', action:'delete', id:target?.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
																	<g:message code="default.link.delete.label" default="Delete" />
																</a>
															</li>
														</ul> 
													</shiro:hasPermission></th>
											</g:else>
										</g:each>
									</tr>
									<tr>
										<g:each in="${dsrTable.targets}" var="target">
											<g:if test="${target.category != null}">
												<th class="title-th">
													<div class="bt">
														<g:i18n field="${target.names}" />
													</div> 
													<shiro:hasPermission permission="admin:dsr">
														<ul class="horizontal">
															<li>
																<a class="edit-link" href="${createLinkWithTargetURI(controller:'dsrTarget', action:'edit', id:target?.id)}">
																	<g:message code="default.link.edit.label" default="Edit" />
																</a>
															</li>
															<li> 
																<a class="delete-link" href="${createLinkWithTargetURI(controller:'dsrTarget', action:'delete', id:target?.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
																	<g:message code="default.link.delete.label" default="Delete" />
																</a>
															</li>
														</ul>
													</shiro:hasPermission>
												</th>
											</g:if>
										</g:each>
									</tr>
								</thead>
								<tbody>
									<g:each in="${dsrTable.organisations}" var="organisation" status="i">
										<g:if test="${dsrTable.organisationMap.get(organisation)!=currentParent}">
											<g:set var="currentParent" value="${dsrTable.organisationMap.get(organisation)}" />
											<tr>
												<th colspan="${dsrTable.targets.size()+1}" class="parent-row">${currentParent.name}</th>
											</tr>
										</g:if>
										<tr class="row organisation" data-group="${organisation.organisationUnitGroup?.uuid}">
											<th class="box-dsr-organisation">${organisation.name}</th>
											<g:each in="${dsrTable.targets}" var="target">
												<td class="box-dsr-value">
													<g:if test="${!dsrTable.getDsr(organisation, target).applies}">
	
													</g:if>
													<g:else>
														${dsrTable.getDsr(organisation, target).stringValue}
													</g:else>
												</td>
											</g:each>
										</tr>
									</g:each>
								</tbody>
							</table>
						</g:if>
						<g:else>
							<div>
								Please <a href="${createLinkWithTargetURI(controller:'dsrTarget', action:'create')}"> Add Target </a>
							</div>
						</g:else>
					</g:if>
					<g:else>
						<p class="help">Please select an Organisation / Objective</p>
					</g:else>
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</body>
</html>