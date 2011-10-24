<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
 		<meta name="layout" content="main" />
		<title>Facility Count Tables</title>
		
		<!-- for admin forms -->
		<shiro:hasPermission permission="admin:fct">
        	<r:require modules="form"/>
        </shiro:hasPermission>        
	</head>
	<body>
		<div id="fct">
			<div class="box margin-bottom-10">
				<g:render template="/templates/iterationFilter" model="[linkParams:[organisation: currentOrganisation?.id, objective: currentObjective?.id]]"/>
				<g:render template="/templates/organisationFilter" model="[linkParams:[period: currentPeriod.id, objective: currentObjective?.id]]"/>
				<g:render template="/templates/objectiveFilter" model="[linkParams:[period: currentPeriod.id, organisation: currentOrganisation?.id]]"/>
				
				<g:if test="${dsrTable != null}">
					<g:render template="/templates/facilityTypeFilter" model="[facilityTypes: dsrTable.facilityTypes]"/>
				</g:if>
				<div class="clear"></div>
				<div>
				<!-- ADMIN SECTION -->
				<shiro:hasPermission permission="admin:fct">
					<span> <a href="${createLinkWithTargetURI(controller:'fctObjective', action:'create')}">Add Objective</a> </span>|
					<span> <a href="${createLinkWithTargetURI(controller:'fctTarget', action:'create')}">Add Target</a> </span>
				</shiro:hasPermission>
				<!-- ADMIN SECTION END -->
			</div>
		</div>
		<div id="center" class="box">
			<div id="values">
				<g:if test="${dsrTable != null}">
					<g:if test="${!dsrTable.targets.empty}">
						<table class="nice-table">
							<thead>
								<tr>
									<th class="object-name-box" rowspan="2">
										<div>
											<g:i18n field="${currentObjective.names}" />
										</div> <shiro:hasPermission permission="admin:fct">
											<span> 
												<a class="edit-link" href="${createLinkWithTargetURI(controller:'fctObjective', action:'edit', id:currentObjective.id)}">
													<g:message code="default.link.edit.label" default="Edit" />
												</a> 
											</span>
											<br />
											<span> 
												<a class="delete-link" href="${createLinkWithTargetURI(controller:'fctObjective', action:'delete', id:currentObjective.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
													<g:message code="default.link.edit.label" default="Delete" />
												</a>
											</span>
										</shiro:hasPermission></th>
									<g:set var="i" value="${0}" />
									<g:each in="${dsrTable.targets}" var="target">
										<th class="title-th" rowspan="2">
											<div class="bt">
												<g:i18n field="${target.names}" />
											</div>
											<shiro:hasPermission permission="admin:fct">
												<span> 
													<a class="edit-link" href="${createLinkWithTargetURI(controller:'fctTarget', action:'edit', id:target.id)}">
														<g:message code="default.link.edit.label" default="Edit" />
													</a> 
												</span>
												<br />
												<span> 
													<a class="delete-link" href="${createLinkWithTargetURI(controller:'fctTarget', action:'delete', id:target.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
														<g:message code="default.link.edit.label" default="Delete" />
													</a>
												</span>
											</shiro:hasPermission>
										</th>
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
												<g:if test="${!dsrTable.getFct(organisation, target) != null}">
													${dsrTable.getFct(organisation, target).value}
												</g:if>
											</td>
										</g:each>
									</tr>
								</g:each>
								
							</tbody>
						</table>
					</g:if>
					<g:else>
						<div>
							Please <a href="${createLinkWithTargetURI(controller:'fctTarget', action:'create')}"> Add Target </a>
						</div>
					</g:else>
				</g:if>
				<g:else>
					<div>Please select an Organisation / Objective</div>
				</g:else>
			</div>
			<!-- ADMIN SECTION -->
			<shiro:hasPermission permission="admin:fct">
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