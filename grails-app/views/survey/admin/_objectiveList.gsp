<g:searchBox controller="question" action="search" params="${[surveyId: survey?.id]}" entityName="Survey Question"/>
<table>
	<thead>
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Organisation Unit Groups</th>
			<th>Number of Sections</th>
			<th>Order</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="objective">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><g:i18n field="${objective.names}" /></td>
				<td><g:i18n field="${objective.descriptions}" /></td>
				<td>${objective.groupUuidString}</td>
				<td>${objective.sections.size()}</td>
				<td>${objective.order}</td>
				<td>
					<div class="dropdown white-dropdown"> 
						<a class="selected" href="#" data-type="objective">Manage</a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'section', action:'list', params:[surveyId:survey?.id,objectiveId: objective.id])}">Sections</a>
								</li>
								<li>
								<g:link
									controller="objective" action="edit" id="${objective.id}"
									class="flow-edit">
									<g:message code="general.text.edit" default="Edit" />
								</g:link>
								</li>
								<li>
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
	</tbody>
</table>