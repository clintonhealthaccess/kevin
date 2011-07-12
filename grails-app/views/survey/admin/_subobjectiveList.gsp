<div class="survey-admin-entity-list">
	<div id="subobjectives">
		<div class="float-right">
			<a id="add-subobjective-link" class="flow-add" href="${createLink(controller:'subobjective', action:'create')}">New Sub-Strategic Objective</a>
		</div>
		<div id="survey-admin-table-list">
			<table>
			 <g:if test="${!subobjectives.isEmpty()}">
					<tr class="table-header">
						<th>Order</th>
						<th>ID</th>
						<th>Name</th>
						<th>Description</th>
						<th>Number of Questions</th>
						<th><g:message code="general.text.edit" default="Edit" /></th>
						<th><g:message code="general.text.delete" default="Delete" /></th>
					</tr>
					<g:each in="${subobjectives}" status="i" var="subobjective">
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td>${subobjective?.order}</td>
							<td>${subobjective.id}</td>
							<td><g:i18n field="${subobjective.names}" />
							</td>
							<td><g:i18n field="${subobjective.descriptions}" />
							</td>
							<td><a
								href="${createLink(controller:'admin', action:'question',params:[survey:survey?.id,objective: objective?.id,subobjective: subobjective.id])}">${subobjective.questions.size()}</a>
							</td>
							<td class="edit-subobjective-link"><a
								href="${createLink(controller:'subobjective', action:'edit',params:[survey:survey?.id,objective: objective?.id,subobjective: subobjective.id])}"><g:message
										code="general.text.edit" default="Edit" /> </as>
							</td>
							<td class="delete-subobjective-link"><a
								href="${createLink(controller:'subobjective', action:'delete',params:[survey:survey?.id,objective: objective?.id,subobjective: subobjective.id])}"><g:message
										code="general.text.delete" default="Delete" /> </as>
							</td>
						</tr>
					</g:each>
					</g:if>
					<g:else>
						<tr>
							<td colspan="7">No sub-objective available <a href="#">Add Sub Objective</a></td>
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
