<div class="survey-admin-entity-list">
	<div id="subobjectives">
	<div class="float-left">
			<h5>Sub-Objective List</h5>
		</div>
		<div class="float-right">
			<a id="add-subobjective-link" class="flow-add" href="${createLink(controller:'subObjective', action:'create', params:[objectiveId: objective.id])}">
			New Sub-Section</a>
		</div>
		<div id="admin-table-list">
			<table>
			 <g:if test="${!subobjectives.isEmpty()}">
					<tr class="table-header">
						<th>Name</th>
						<th>Description</th>
						<th>Organisation Unit Groups</th>
						<th>Number of Questions</th>
						<th>Order</th>
						<th>Manage</th>
					</tr>
					<g:each in="${subobjectives}" status="i" var="subobjective"> 
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td><g:i18n field="${subobjective.names}" />
							</td>
							<td><g:i18n field="${subobjective.descriptions}" />
							</td>
							<td>${subobjective.groupUuidString}
							</td>
							<td>${subobjective.questions.size()}
							</td>
							<td>${subobjective.order}</td>
						  <td>
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="subobjective">Manage</a>
							<div class="hidden dropdown-list">
								<ul>
								<li>
								<a
								href="${createLink(controller:'admin', action:'question',params:[surveyId:objective.survey?.id,objectiveId: objective?.id,subObjectiveId: subobjective.id])}">Questions</a>
								
								</li>
									<li class="edit-subobjective-link">
							        <g:link controller="subObjective" action="edit" id="${subobjective.id}" class="flow-edit">
									<g:message code="general.text.edit" default="Edit" />
								</g:link>
									</li>
									<li class="delete-subobjective-link">
							      <g:link controller="subObjective" action="delete" id="${subobjective.id}" class="flow-delete">
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
							<td colspan="6">No sub-objective available 
							<a id="new-subobjective-link" class="flow-add" href="${createLink(controller:'subObjective', action:'create',params:[objectiveId: objective.id])}">
							New Sub-Strategic Objective</a>
							</td>
						</tr>
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${subobjectiveCount}" />
		</div>
		<div class="hidden flow-container"></div>
	</div>
	<div class="clear"></div>
</div>
