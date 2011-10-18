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
				<div class="filter">
					<span class="bold">Iteration:</span>
					<span class="dropdown subnav-dropdown">
						<a class="selected" href="#" data-period="${currentPeriod.id}" data-type="period">
							<g:dateFormat format="yyyy" date="${currentPeriod.startDate}" />
						</a>
						<div class="hidden dropdown-list">
							<ul>
								<g:each in="${periods}" var="period">
									<li><a href="${createLink(controller:'dsr', action:'view', params:[period:period.id, objective: currentObjective?.id, organisation: currentOrganisation?.id])}">
										<span><g:dateFormat format="yyyy" date="${period.startDate}" /></span> 
										</a>
									</li>
								</g:each>
							</ul>
						</div>
					</span>
				</div>
				<div class="filter">
					<span class="bold">Organisation:</span>
					<span class="dropdown subnav-dropdown">
						<g:if test="${currentOrganisation != null}">
							<a class="selected" href="#" data-type="organisation">${currentOrganisation.name}</a>
						</g:if>
						<g:else>
							<a class="selected" href="#" data-type="organisation">Select Organisation Unit</a>
						</g:else> 
						<div class="hidden dropdown-list">
							<ul>
								<g:render template="/templates/organisationTree"
									model="[controller: 'dsr', action: 'view', organisation: organisationTree, current: currentOrganisation, params:[period: currentPeriod.id, objective: currentObjective?.id], displayLinkUntil: displayLinkUntil]" />
							</ul>
						</div>
					</span>
				</div>
				<div class="filter">
					<span class="bold">Strategic Objective:</span>
					<span class="dropdown subnav-dropdown">
						<g:if test="${currentObjective != null}">
							<a class="selected" href="#"
							data-organisation="${currentObjective.id}"
							data-type="objective"><g:i18n field="${currentObjective.names}"/></a>
						</g:if>
						<g:else>
							<a href="#" class="selected" data-type="objective">
							Select Strategic Objective</a>
						</g:else>
						<div class="hidden dropdown-list">
							<g:if test="${!objectives.empty}">
								<ul>
									<g:each in="${objectives}" var="objective">
										<li>
											<span>
												<a href="${createLink(controller: 'dsr', action:'view', params:[period: currentPeriod.id, objective: objective?.id, organisation: currentOrganisation?.id])}">
													<g:i18n field="${objective.names}"/>
												</a>
											</span>
								    		<shiro:hasPermission permission="admin:dsr">
												<span>
													<a href="${createLinkWithTargetURI(controller:'dsrObjective', action:'edit', id:objective.id)}">(Edit)</a>
												</span>
												<span>
													<a href="${createLinkWithTargetURI(controller:'dsrObjective', action:'delete', id:objective.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
														(Delete)
													</a>
												</span>
											</shiro:hasPermission>
										</li>
									</g:each>
								</ul>
							</g:if>
							<g:else>
								<span>No Objectives Found</span>
							</g:else>
						</div>
					</span>
				</div>
				<div class="right">
					<!-- ADMIN SECTION -->
					<shiro:hasPermission permission="admin:dsr">
						<span> <a href="${createLinkWithTargetURI(controller:'dsrObjective', action:'create')}">Add Objective</a> </span>|
						<span> <a href="${createLinkWithTargetURI(controller:'dsrTarget', action:'create')}">Add Target</a> </span>|
						<span> <a href="${createLinkWithTargetURI(controller:'dsrTargetCategory', action:'create')}">Add Target Category</a> </span>
					</shiro:hasPermission>
					<!-- ADMIN SECTION END -->
				</div>
			</div>
			<g:if test="${dsrTable != null}">
				<div class="filter facility-type">
		    		<h4 class="bold">Facility types</h4>
		    		<div id="facility-type-filter">
			    		<g:if test="${!dsrTable.facilityTypes.isEmpty()}">
			    		  <ul>
	  			    		<g:each in="${dsrTable.facilityTypes}" var="group">
	  				    		<li><input type="checkbox" value="${group.uuid}" ${checkedFacilities.contains(group.uuid)?'checked="checked"':'""'}/>${group.name}</li>
	  			    		</g:each>
	  			    	</ul>
			    		</g:if>
		    		<g:else>
		    			<span class="italic">No facility types</span>
		    		</g:else>
		    		</div>
		    	</div>
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
													<a href="${createLinkWithTargetURI(controller:'dsrObjective', action:'edit', id:currentObjective.id)}">(Edit)</a> 
												</span>
												<br />
												<span> 
													<a href="${createLinkWithTargetURI(controller:'dsrObjective', action:'delete', id:currentObjective.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
														(Delete)
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
														<div>
															<g:i18n field="${target.category.names}" />
														</div> 
														<shiro:hasPermission permission="admin:dsr">
															<span> 
																<a href="${createLinkWithTargetURI(controller:'dsrTargetCategory', action:'edit', id:target?.id)}">(Edit)</a> 
															</span>
															<br />
															<span> 
																<a href="${createLinkWithTargetURI(controller:'dsrTargetCategory', action:'delete', id:target?.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
																	(Delete)
																</a> 
															</span>
														</shiro:hasPermission>
													</th>
												</g:if>
											</g:if>
											<g:else>
												<th class="title-th" rowspan="2">
													<div class="bt">
														<g:i18n field="${target.names}" />
													</div>
													<shiro:hasPermission permission="admin:dsr">
														<span> 
															<a href="${createLinkWithTargetURI(controller:'dsrTarget', action:'edit', id:target?.id)}">(Edit)</a> 
														</span>
														<br />
														<span> 
															<a href="${createLinkWithTargetURI(controller:'dsrTarget', action:'delete', id:target?.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
																(Delete)
															</a> 
														</span>
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
														<span> 
															<a href="${createLinkWithTargetURI(controller:'dsrTarget', action:'edit', id:target?.id)}">(Edit)</a> 
														</span>
														<br />
														<span> 
															<a href="${createLinkWithTargetURI(controller:'dsrTarget', action:'delete', id:target?.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
																(Delete)
															</a> 
														</span>
													</shiro:hasPermission></th>
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