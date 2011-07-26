<div class="survey-admin-entity-list">
	<div id="questions">
		<div class="float-right">
			<a id="add-section-link" class="flow-add" href="${createLink(controller:'question', action:'create')}">New Question</a>
		</div>
		<div id="survey-admin-table-list">
			<table>
			 <g:if test="${!questions.isEmpty()}">
					<tr class="table-header">
						<th>Order</th>
						<th>ID</th>
						<th>Name</th>
						<th>Description</th>
						<th>Type</th>
						<th><g:message code="general.text.edit" default="Edit" /></th>
						<th><g:message code="general.text.delete" default="Delete" /></th>
					</tr>
					<g:each in="${questions}" status="i" var="question">
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td>${question?.order}</td>
							<td>${question.id}</td>
							<td><g:i18n field="${question.names}" /></td>
							<td><g:i18n field="${question.descriptions}" /></td>
							<td>${question.getTemplate()}</td>
							<td class="edit-question-link"><a
								href="${createLink(controller:'question', action:'edit',params:[survey:survey?.id,objective: objective?.id, section: section?.id,question: question?.id])}"><g:message
										code="general.text.edit" default="Edit" /> </as>
							</td>
							<td class="delete-question-link"><a
								href="${createLink(controller:'question', action:'delete',params:[survey:survey?.id,objective: objective?.id, section: section?.id,question: question?.id])}"><g:message
										code="general.text.delete" default="Delete" /> </as>
							</td>
						</tr>
					</g:each>
					</g:if>
					<g:else>
							<tr>
							<td colspan="7">No question available <a href="#">Add Section</a></td>
						</tr>
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${questionCount}" />
		</div>
		<div class="hidden flow-container"></div>
	</div>
	<div class="clear"></div>
</div>
