<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="dsrTable.view.label" default="District Summary Reports" /></title></head>
<body>
	<div id="dsr">
		<div id="top" class="box">
			<div class="filter">
				<h5>Iteration:</h5>
				<div class="dropdown">
					<a class="selected" href="#" data-period="${dsrTable.period.id}"
						data-type="period"><g:dateFormat format="yyyy"
							date="${dsrTable.period.startDate}" /></a>
					<div class="hidden dropdown-list">
						<ul>
							<g:each in="${periods}" var="period">
								<li><a href="${createLink(controller: "dsr", action:"view", params:[period:period.id, objective: dsrTable.objective?.id, organisation: dsrTable.organisation?.id])}">
										<span><g:dateFormat format="yyyy"
												date="${period.startDate}" />
									</span> </a></li>
							</g:each>
						</ul>
					</div>
				</div>
			</div>
			<div class="filter">
				<h5>Organisation:</h5>
				<div class="dropdown">
					<g:if test="${dsrTable.organisation != null}">
						<a class="selected" href="#"
							data-organisation="${dsrTable.organisation.id}"
							data-type="organisation">${dsrTable.organisation.name}</a>
					</g:if>
					<g:else>
						<a class="selected" href="#" data-type="organisation">Select
							Organisation Unit</a>
					</g:else>
					<div class="hidden dropdown-list">
						<ul>
							<g:render template="/templates/organisationTree"
								model="[controller: 'dsr', action: 'view', organisation: organisationTree, params:[period: periods.startDate, objective: objective?.id], displayLinkUntil: 3]" />
						</ul>
					</div>
				</div>
			</div>
			<div class="filter">
				<h5>Strategic Objective:</h5>
					<div class="dropdown">
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
												<a href="${createLink(controller: "dsr", action:"view", params:[period: dsrTable.period.id, objective: objective.id, organisation: dsrTable.organisation.id])}">
													<g:i18n field="${objective.names}"/>
												</a>
											</span>
											<span>
												<g:link controller="dsrObjective" action="edit" id="${objective.id}" class="flow-edit">(Edit)</g:link>
											</span>
										</li>
									</g:each>
								</ul>
							</g:if>
							<g:else>
								<span>No Objectives Found</span>
							</g:else>
						</div>
					</div>
			</div>
			<!-- ADMIN SECTION -->
				<g:if test="${true || user.admin}">
					<div>
						<a id="add-dsr-objective-link" class="flow-add"  href="${createLink(controller:'dsrObjective', action:'create')}">Add Objective</a>
					</div>
					<div>
						<a id="add-dsr-target-link" class="flow-add" href="${createLink(controller:'dsrTarget', action:'create')}">Add Target</a>
					</div>
					<div>
						<a id="add-dsr-category-link" class="flow-add"  href="${createLink(controller:'dsrTargetCategory', action:'create')}">Add Target Category</a>
					</div>						
				</g:if>
		 <!-- ADMIN SECTION END -->
		</div>
		<div id="center" class="box">
			<div id="values">
			<g:if test="${dsrTable.objective != null && dsrTable.organisation != null}">
			<g:if test="${!dsrTable.targets.empty}">
				<table class="nice-table">
				<tbody>
					<tr>					
						<th class="object-name-box" rowspan="2">
						<g:i18n field="${dsrTable.objective.names}"/>
						<span>
						<g:link controller="dsrObjective" action="delete" id="${dsrTable.objective.id}" class="flow-delete">(Delete)</g:link>
					    </span><br/>
						<span>
						<g:link controller="dsrObjective" action="edit" id="${dsrTable.objective.id}" class="flow-edit">(Edit)</g:link>
					    </span>
						</th>
						<g:set var="i" value="${0}" />
						<g:each in="${dsrTable.targets}" var="target">
							<g:if test="${target.category != null}">
								<g:set var="i" value="${i+1}" />
								<g:if test="${i==target.category.getTargetsForObjective(dsrTable.objective).size()}">
									<th class="title-th" colspan="${i}">
										<g:i18n field="${target.category.names}"/><br/>
										<g:if test="${true || user.admin}">
										<span>
										 <a id="delete-dsr-target-category-link" class="flow-delete" href="${createLink(controller:'dsrTargetCategory', action:'delete', params:[id: target.category?.id])}">
										   (Delete)
										   </a>
										   </span><br/>
										   <span>
										   <a id="edit-dsr-target-category-link" class="flow-add" href="${createLink(controller:'dsrTargetCategory', action:'edit', params:[id: target.category?.id])}">
										   (Edit)
										   </a>
										</span>
										</g:if>
										<br/>
									</th>
									<g:set var="i" value="${0}" />
								</g:if>
							</g:if>
							<g:else>
								<th class="title-th" rowspan="2">
								<g:i18n field="${target.names}"/>
								<g:if test="${true || user.admin}"><br/>
								<span>
								   <a id="delete-dsr-target-link" class="flow-delete" href="${createLink(controller:'dsrTarget', action:'delete', params:[id: target?.id])}">(Delete)</a>
								</span><br/>
								<span>
								   <a id="edit-dsr-target-link" class="flow-add" href="${createLink(controller:'dsrTarget', action:'edit', params:[id: target?.id])}">(Edit)</a>
								</span>
								</g:if>
								</th>
							</g:else>
						</g:each>
					</tr>
					<tr>
						<g:each in="${dsrTable.targets}" var="target">
							<g:if test="${target.category != null}">
								<th class="title-th">							
									<g:i18n field="${target.names}"/>
									<g:if test="${true || user.admin}"><br/>
									<span>
									   <a id="delete-dsr-target-link" class="flow-delete" href="${createLink(controller:'dsrTarget', action:'delete', params:[id: target?.id])}">(Delete)</a>
									</span><br/>
									<span>
									   <a id="edit-dsr-target-link" class="flow-add" href="${createLink(controller:'dsrTarget', action:'edit', params:[id: target?.id])}">(Edit)</a>
									</span>
									</g:if>
								</th>
							</g:if>
						</g:each>
					</tr>
					<g:each in="${dsrTable.organisations}" var="organisation">
						<tr>
						<th class="box-dsr-organisation">${organisation.name}</th>
							<g:each in="${dsrTable.targets}" var="target">
								<td class="box-dsr-value">
									${dsrTable.getDsrValue(organisation, target)}
								</td>
							</g:each>
						</tr>
					</g:each>
					</tbody>
				</table>
					</g:if>
				<g:else>
						<div>
						Please <a id="add-dsr-target-link" class="flow-add" href="${createLink(controller:'dsrTarget', action:'create')}">
						Add Target
						</a>
						</div>
					</g:else>
				</g:if>
					<g:else>
						<div>Please select an Organisation / Objective</div>
					</g:else>
			</div>
				<!-- ADMIN SECTION -->
		    	<g:if test="${true || user.admin}">
	    			<div class="hidden flow-container"></div>
					<script type="text/javascript">
						$(document).ready(function() {
							$('#values').flow({
								onSuccess: function(data) {
									if (data.result == 'success') {
										location.reload();
									}
								}
							});
						});

					</script>
		    	</g:if>
		    	<!-- ADMIN SECTION END -->
				<div class="clear"></div>
		</div>
	</div>
</body>
</html>