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
			<div class="box margin-bottom-10">
				<div class="filter">
					<span class="bold">Iteration:</span>
					<span class="dropdown white-dropdown">
						<a class="selected" href="#" data-period="${dsrTable.period.id}" data-type="period">
							<g:dateFormat format="yyyy" date="${dsrTable.period.startDate}" />
						</a>
						<div class="hidden dropdown-list">
							<ul>
								<g:each in="${periods}" var="period">
									<li><a href="${createLink(controller: "dsr", action:"view", params:[period:period.id, objective: dsrTable.objective?.id, organisation: dsrTable.organisation?.id])}">
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
					<span class="dropdown white-dropdown">
						<g:if test="${dsrTable.organisation != null}">
							<a class="selected" href="#" data-type="organisation">${dsrTable.organisation.name}</a>
						</g:if>
						<g:else>
							<a class="selected" href="#" data-type="organisation">Select Organisation Unit</a>
						</g:else> 
						<div class="hidden dropdown-list">
							<ul>
								<g:render template="/templates/organisationTree"
									model="[controller: 'dsr', action: 'view', organisation: organisationTree, current: dsrTable.organisation, params:[period: periods.startDate, objective: dsrTable.objective?.id], displayLinkUntil: displayLinkUntil]" />
							</ul>
						</div>
					</span>
				</div>
				<div class="filter">
					<span class="bold">Strategic Objective:</span>
					<span class="dropdown white-dropdown">
						<g:if test="${dsrTable.objective != null}">
							<a class="selected" href="#"
							data-organisation="${dsrTable.objective.id}"
							data-type="objective"><g:i18n field="${dsrTable.objective.names}"/></a>
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
												<a href="${createLink(controller: "dsr", action:"view", params:[period: dsrTable.period.id, objective: objective?.id, organisation: dsrTable.organisation?.id])}">
													<g:i18n field="${objective.names}"/>
												</a>
											</span>
								    		<shiro:hasPermission permission="admin:dsr">
												<span>
													<g:link controller="dsrObjective" action="edit" id="${objective.id}" class="flow-edit">(Edit)</g:link>
												</span>
												<span>
													<g:link controller="dsrObjective" action="delete" id="${objective.id}" class="flow-delete">(Delete)</g:link>
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
				<g:if test="${dsrTable.objective != null && dsrTable.organisation != null}">
					<div class="clear"></div>
					<div class="filter margin-top-20">
			    		<div class="bold">Facility types</div>
			    		<div id="facility-type-filter">
				    		<g:if test="${!dsrTable.facilityTypes.isEmpty()}">
					    		<g:each in="${dsrTable.facilityTypes}" var="group">
						    		<input type="checkbox" value="${group.uuid}" ${checkedFacilities.contains(group.uuid)?'checked="checked"':'""'}/>${group.name}
					    		</g:each>
				    		</g:if>
			    		<g:else>
			    			<span class="italic">No facility types</span>
			    		</g:else>
			    		</div>
			    		<div class="clear"></div>
			    	</div>
				</g:if>
				<div class="clear"></div>
				<div>
				<!-- ADMIN SECTION -->
				<shiro:hasPermission permission="admin:dsr">
					<span> <a id="add-dsr-objective-link" class="flow-add" href="${createLink(controller:'dsrObjective', action:'create')}">Add Objective</a> </span>|
						<span> <a id="add-dsr-target-link" class="flow-add" href="${createLink(controller:'dsrTarget', action:'create')}">Add Target</a> </span>|
						<span> <a id="add-dsr-category-link" class="flow-add" href="${createLink(controller:'dsrTargetCategory', action:'create')}">Add Target Category</a> </span>
				</shiro:hasPermission>
				<!-- ADMIN SECTION END -->
			</div>
		</div>
		<div id="center" class="box">
			<div id="values">
				<g:if test="${dsrTable.objective != null && dsrTable.organisation != null}">
					<g:if test="${!dsrTable.targets.empty}">
						<table class="nice-table">
							<tbody>
								<tr>
									<th class="object-name-box" rowspan="2">
										<div>
											<g:i18n field="${dsrTable.objective.names}" />
										</div> <shiro:hasPermission permission="admin:dsr">
											<span> <g:link controller="dsrObjective" action="delete" id="${dsrTable.objective.id}" class="flow-delete">(Delete)</g:link> </span>
											<br />
											<span> <g:link controller="dsrObjective" action="edit" id="${dsrTable.objective.id}" class="flow-edit">(Edit)</g:link> </span>
										</shiro:hasPermission></th>
									<g:set var="i" value="${0}" />
									<g:each in="${dsrTable.targets}" var="target">
										<g:if test="${target.category != null}">
											<g:set var="i" value="${i+1}" />
											<g:if test="${i==target.category.getTargetsForObjective(dsrTable.objective).size()}">
												<th class="title-th" colspan="${i}">
													<div>
														<g:i18n field="${target.category.names}" />
													</div> 
													<shiro:hasPermission permission="admin:dsr">
														<span> <a id="delete-dsr-target-category-link" class="flow-delete" href="${createLink(controller:'dsrTargetCategory', action:'delete', params:[id: target.category?.id])}"> (Delete) </a> </span>
														<br />
														<span> <a id="edit-dsr-target-category-link" class="flow-add" href="${createLink(controller:'dsrTargetCategory', action:'edit', params:[id: target.category?.id])}"> (Edit) </a> </span>
													</shiro:hasPermission> <br /></th>
												<g:set var="i" value="${0}" />
											</g:if>
										</g:if>
										<g:else>
											<th class="title-th" rowspan="2">
												<div class="bt">
													<g:i18n field="${target.names}" />
												</div>
												<shiro:hasPermission permission="admin:dsr">
													<span> <a id="delete-dsr-target-link" class="flow-delete" href="${createLink(controller:'dsrTarget', action:'delete', params:[id: target?.id])}">(Delete)</a> </span>
													<br />
													<span> <a id="edit-dsr-target-link" class="flow-add" href="${createLink(controller:'dsrTarget', action:'edit', params:[id: target?.id])}">(Edit)</a> </span>
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
													<span> <a id="delete-dsr-target-link" class="flow-delete" href="${createLink(controller:'dsrTarget', action:'delete', params:[id: target?.id])}">(Delete)</a> </span>
													<br />
													<span> <a id="edit-dsr-target-link" class="flow-add" href="${createLink(controller:'dsrTarget', action:'edit', params:[id: target?.id])}">(Edit)</a> </span>
												</shiro:hasPermission></th>
										</g:if>
									</g:each>
								</tr>
								
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
							Please <a id="add-dsr-target-link" class="flow-add" href="${createLink(controller:'dsrTarget', action:'create')}"> Add Target </a>
						</div>
					</g:else>
				</g:if>
				<g:else>
					<div>Please select an Organisation / Objective</div>
				</g:else>
			</div>
			<!-- ADMIN SECTION -->
			<shiro:hasPermission permission="admin:dsr">
				<div class="hidden flow-container"></div>
				<r:script>
						$(document).ready(function() {
							$('#values').flow({
								onSuccess: function(data) {
									if (data.result == 'success') {
										location.reload();
									}
								}
							});
						});
					</r:script>
			</shiro:hasPermission>
			<!-- ADMIN SECTION END -->
			<div class="clear"></div>
		</div>
	</div>

</body>
</html>