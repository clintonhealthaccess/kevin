<div class="survey-admin-entity-list">
	<div id="objectives">
	<div class="float-left">
			<h5>Objective List</h5>
		</div>
		<div class="float-right">
			<a id="add-objective-link" class="flow-add"
				href="${createLink(controller:'objective', action:'create',params:[surveyId:survey.id])}">New Objective</a>
		</div>
		<div id="admin-table-list">
			<table>
				<g:if test="${!objectives.isEmpty()}">
					<tr class="table-header">
						<th>Name</th>
						<th>Description</th>
						<th>Organisation Unit Groups</th>
						<th>Number of Sections</th>
						<th>Order</th>
						<th>Manage</th>
					</tr>
					<g:each in="${objectives}" status="i" var="objective">
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td><g:i18n field="${objective.names}" /></td>
							<td><g:i18n field="${objective.descriptions}" /></td>
							<td>${objective.groupUuidString}</td>
							<td>${objective.sections.size()}</td>
							<td>${objective.order}</td>
                           <td>
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="objective">Manage</a>
							<div class="hidden dropdown-list">
								<ul>
								<li>
								<a
								href="${createLink(controller:'admin', action:'section',params:[surveyId:survey?.id,objectiveId: objective.id])}">Sections</a>
								
								</li>
									<li class="edit-objective-link">
									<g:link
									controller="objective" action="edit" id="${objective.id}"
									class="flow-edit">
									<g:message code="general.text.edit" default="Edit" />
								</g:link>
									</li>
									<li class="delete-objective-link">
									<g:link controller="objective" action="delete" id="${objective.id}" class="flow-delete">
									<g:message code="general.text.delete" default="Delete" />
								</g:link>
									</li>
								</ul>
							</div>
							</div> 		
							</td>
						</tr>
					</g:each>
				</g:if>
				<g:else>
					<tr>
						<td colspan="6">No objective available <a id="new-objective-link" class="flow-add"
				href="${createLink(controller:'objective', action:'create',params:[surveyId:survey.id])}">Add
								Objective</a>
						</td>
					</tr>
				</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${objectiveCount}" />
		</div>
		<div class="hidden flow-container"></div>
	</div>
	<div class="clear"></div>
</div>
