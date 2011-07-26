<div class="survey-admin-entity-list">
	<div id="sections">
		<div class="float-right">
			<a id="add-section-link" class="flow-add" href="${createLink(controller:'section', action:'create')}">New Section Objective</a>
		</div>
		<div id="survey-admin-table-list">
			<table>
			 <g:if test="${!sections.isEmpty()}">
					<tr class="table-header">
						<th>Order</th>
						<th>ID</th>
						<th>Name</th>
						<th>Description</th>
						<th>Number of Questions</th>
						<th><g:message code="general.text.edit" default="Edit" /></th>
						<th><g:message code="general.text.delete" default="Delete" /></th>
					</tr>
					<g:each in="${sections}" status="i" var="section">
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td>${section?.order}</td>
							<td>${section.id}</td>
							<td><g:i18n field="${section.names}" />
							</td>
							<td><g:i18n field="${section.descriptions}" />
							</td>
							<td><a
								href="${createLink(controller:'admin', action:'question',params:[survey:survey?.id,objective: objective?.id,section: section.id])}">${section.questions.size()}</a>
							</td>
							<td class="edit-section-link"><a
								href="${createLink(controller:'section', action:'edit',params:[survey:survey?.id,objective: objective?.id,section: section.id])}"><g:message
										code="general.text.edit" default="Edit" /> </as>
							</td>
							<td class="delete-section-link"><a
								href="${createLink(controller:'section', action:'delete',params:[survey:survey?.id,objective: objective?.id,section: section.id])}"><g:message
										code="general.text.delete" default="Delete" /> </as>
							</td>
						</tr>
					</g:each>
					</g:if>
					<g:else>
						<tr>
							<td colspan="7">No section available <a href="#">Add Section</a></td>
						</tr>
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${sectionCount}" />
		</div>
		<div class="hidden flow-container"></div>
	</div>
	<div class="clear"></div>
</div>
