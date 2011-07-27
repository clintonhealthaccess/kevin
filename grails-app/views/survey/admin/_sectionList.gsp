<div class="survey-admin-entity-list">
	<div id="sections">
	<div class="float-left">
			<h5>Section List</h5>
		</div>
		<div class="float-right">
			<a id="add-section-link" class="flow-add" href="${createLink(controller:'section', action:'create', params:[objectiveId: objective.id])}">
			New Section</a>
		</div>
		<div id="admin-table-list">
			<table>
			 <g:if test="${!sections.isEmpty()}">
					<tr class="table-header">
						<th>Name</th>
						<th>Description</th>
						<th>Organisation Unit Groups</th>
						<th>Number of Questions</th>
						<th>Order</th>
						<th>Manage</th>
					</tr>
					<g:each in="${sections}" status="i" var="section"> 
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td><g:i18n field="${section.names}" />
							</td>
							<td><g:i18n field="${section.descriptions}" />
							</td>
							<td>${section.groupUuidString}
							</td>
							<td>${section.questions.size()}
							</td>
							<td>${section.order}</td>
						  <td>
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="section">Manage</a>
							<div class="hidden dropdown-list">
								<ul>
								<li>
								<a
								href="${createLink(controller:'admin', action:'question',params:[surveyId:objective.survey?.id,objectiveId: objective?.id,sectionId: section.id])}">Questions</a>
								
								</li>
									<li class="edit-section-link">
							        <g:link controller="section" action="edit" id="${section.id}" class="flow-edit">
									<g:message code="general.text.edit" default="Edit" />
								</g:link>
									</li>
									<li class="delete-section-link">
							      <g:link controller="section" action="delete" id="${section.id}" class="flow-delete">
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
							<td colspan="6">No section available 
							<a id="new-section-link" class="flow-add" href="${createLink(controller:'section', action:'create',params:[objectiveId: objective.id])}">
							New Section</a>
							</td>
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
