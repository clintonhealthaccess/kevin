<table>
	<thead>
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Organisation Unit Groups</th>
			<th>Number of Questions</th>
			<th>Order</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="section"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><g:i18n field="${section.names}" /></td>
				<td><g:i18n field="${section.descriptions}" /></td>
				<td>${section.groupUuidString}</td>
				<td>${section.questions.size()}</td>
				<td>${section.order}</td>
				<td>
					<div class="dropdown white-dropdown"> 
						<a class="selected" href="#" data-type="section">Manage</a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'question', action:'list',params:[surveyId:objective.survey?.id,objectiveId: objective?.id,sectionId: section.id])}">Questions</a>
								</li>
								<li>
				    			    <g:link controller="section" action="edit" id="${section.id}" class="flow-edit">
										<g:message code="general.text.edit" default="Edit" />
									</g:link>
								</li>
								<li>
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
	</tbody>
</table>
