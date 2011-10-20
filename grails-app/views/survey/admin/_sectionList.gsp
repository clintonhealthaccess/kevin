<table>
	<thead>
		<tr>
			<th><g:message code="general.text.name" default="Name"/></th>
			<th><g:message code="general.text.description" default="Description"/></th>
			<th><g:message code="general.text.facilitygroups" default="Facility Groups"/></th>
			<th><g:message code="survey.numberofquestions.label" default="Number of Questions"/></th>
			<th><g:message code="general.text.order" default="Order"/></th>
			<th><g:message code="general.text.manage" default="Manage"/></th>
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
						<a class="selected" href="#" data-type="section"><g:message code="general.text.manage" default="Manage"/></a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'question', action:'list',params:[surveyId:objective.survey?.id,objectiveId: objective?.id,sectionId: section.id])}">
										<g:message code="default.list.label" args="${[message(code:'general.text.question',default:'Questions')]}" />
									</a>
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
