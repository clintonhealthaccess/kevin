<g:searchBox controller="question" action="search" params="${[surveyId: survey?.id]}" entityName="Survey Question"/>
<table>
	<thead>
		<tr>
			<th><g:message code="general.text.name" default="Name"/></th>
			<th><g:message code="general.text.description" default="Description"/></th>
			<th><g:message code="general.text.facilitygroups" default="Facility Groups"/></th>
			<th><g:message code="survey.numberofsections.label" default="Number of Sections"/></th>
			<th><g:message code="general.text.order" default="Order"/></th>
			<th><g:message code="general.text.manage" default="Manage"/></th>
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
						<a class="selected" href="#" data-type="objective"><g:message code="general.text.manage" default="Manage"/></a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'section', action:'list', params:[surveyId:survey?.id,objectiveId: objective.id])}">
										<g:message code="default.list.label" args="${[message(code:'general.text.section',default:'Section')]}" />
									</a>
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
