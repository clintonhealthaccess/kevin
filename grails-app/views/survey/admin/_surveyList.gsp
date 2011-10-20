<table>
	<thead>
		<tr>
			<th><g:message code="general.text.name" default="Name"/></th>
			<th><g:message code="general.text.active" default="Active"/></th>
			<th><g:message code="general.text.description" default="Description"/></th>
			<th><g:message code="general.text.period" default="Period"/></th>
			<th><g:message code="general.text.numberofobjectives" default="Number of Objectives"/></th>
			<th><g:message code="general.text.manage" default="Manage"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="survey">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><g:i18n field="${survey.names}" /></td>
				<td>${survey?.active?'\u2713':''}</td>
				<td><g:i18n field="${survey.descriptions}" /></td>
				<td>[${survey.period.startDate} - ${survey.period.startDate}]</td>
				<td>${survey.objectives.size()}</td>
				<td>
					<div class="dropdown white-dropdown"> 
						<a class="selected" href="#" data-type="survey"><g:message code="general.text.manage" default="Manage"/></a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'objective', action:'list', params:[surveyId:survey?.id])}"><g:message code="default.list.label" args="${[message(code:'general.text.objective',default:'Objective')]}" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'surveySkipRule', action:'list', params:[surveyId: survey?.id])}"><g:message code="survey.skiprules.label" default="Skip Rules"/></a>
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
