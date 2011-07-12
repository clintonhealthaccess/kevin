<div class="survey-admin-entity-list">
	<div id="objectives">
		<div class="float-right">
			<a id="add-objective-link" class="flow-add" href="${createLink(controller:'objective', action:'create')}">New Strategic Objective</a>
		</div>
		<div id="survey-admin-table-list">
			<table>
			<g:if test="${!objectives.isEmpty()}">
					<tr class="table-header">
					    <th>Order</th>
						<th>ID</th>
						<th>Name</th>
						<th>Description</th>
						<th>Number of Sub-Objectives</th>
						<th><g:message code="general.text.edit" default="Edit" /></th>
						<th><g:message code="general.text.delete" default="Delete" /></th>
					</tr>
					<g:each in="${objectives}" status="i" var="objective">
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td>${objective?.order}</td>
							<td>${objective.id}</td>
							<td><g:i18n field="${objective.names}" />
							</td>
							<td><g:i18n field="${objective.descriptions}" />
							</td>
							<td><a
								href="${createLink(controller:'admin', action:'subobjective',params:[survey:survey?.id,objective: objective.id])}">${objective.subObjectives.size()}</a>
							</td>
							<td class="edit-survey-link"><a
								href="${createLink(controller:'objective', action:'edit',params:[survey:survey?.id,objective: objective.id])}"><g:message
										code="general.text.edit" default="Edit" />
									</as>
							</td>
							<td class="delete-survey-link"><a
								href="${createLink(controller:'objective', action:'delete',params:[survey:survey?.id,objective: objective.id])}"><g:message
										code="general.text.delete" default="Delete" />
									</as>
							</td>
						</tr>
					</g:each>
					</g:if>
					<g:else>
						<tr>
							<td colspan="7">No objective available <a href="#">Add Objective</a></td>
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
