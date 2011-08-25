<table>
	<thead>
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Period</th>
			<th>Number Objectives</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="survey">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><g:i18n field="${survey.names}" /></td>
				<td><g:i18n field="${survey.descriptions}" /></td>
				<td>[${survey.period.startDate} - ${survey.period.startDate}]</td>
				<td>${survey.objectives.size()}</td>
				<td>
					<div class="dropdown white-dropdown"> 
						<a class="selected" href="#" data-type="survey">Manage</a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'objective', action:'list', params:[surveyId:survey?.id])}">Objectives</a>
								</li>
								<li>
									<g:link controller="survey" action="edit" id="${survey.id}" class="flow-edit">
								    	<g:message code="general.text.edit" default="Edit" /> 
									</g:link>
								</li>
								<li>
							    	<g:link controller="survey" action="delete" id="${survey.id}" class="flow-delete">
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
