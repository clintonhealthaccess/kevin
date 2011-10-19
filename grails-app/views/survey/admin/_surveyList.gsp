<table class="listing">
	<thead>
		<tr>
			<th/>
			<th>Name</th>
			<th>Active</th>
			<th>Description</th>
			<th>Period</th>
			<th>Number Objectives</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="survey">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'survey', action:'edit', params:[id: survey.id])}">
								<g:message code="general.text.edit" default="Edit" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'survey', action:'delete', params:[id: survey.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="general.text.delete" default="Delete" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${survey.names}" /></td>
				<td>${survey?.active?'\u2713':''}</td>
				<td><g:i18n field="${survey.descriptions}" /></td>
				<td>[${survey.period.startDate} - ${survey.period.startDate}]</td>
				<td>${survey.objectives.size()}</td>
				<td>
					<div class="dropdown subnav-dropdown"> 
						<a class="selected" href="#" data-type="survey">Manage</a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'objective', action:'list', params:[surveyId:survey?.id])}">Objectives</a>
								</li>
								<li>
									<a href="${createLink(controller:'surveySkipRule', action:'list', params:[surveyId: survey?.id])}">Skip Rules</a>
								</li>
								<li>
									<a href="${createLink(controller:'surveyValidationRule', action:'list', params:[surveyId: survey?.id])}">Validation Rules</a>
								</li>
								<li>
							    	<g:link controller="survey" action="clone" id="${survey.id}">
								        <g:message code="general.text.clone" default="Clone" /> 
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
