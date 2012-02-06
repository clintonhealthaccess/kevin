<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label" default="Name"/></th>
			<th><g:message code="survey.active.label" default="Active"/></th>
			<th><g:message code="entity.description.label" default="Description"/></th>
			<th><g:message code="period.label" default="Period"/></th>
			<th><g:message code="default.number.label" args="[message(code:'survey.objective.label')]" default="Number of Objectives"/></th>
			<th><g:message code="entity.list.manage.label" default="Manage"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="survey">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'survey', action:'edit', params:[id: survey.id])}">
								<g:message code="default.link.edit.label" default="Edit" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'survey', action:'delete', params:[id: survey.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="default.link.delete.label" default="Delete" /> 
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
					<div class="js_dropdown dropdown"> 
						<a class="selected manage-btn" href="#" data-type="survey"><g:message code="entity.list.manage.label" default="Manage"/></a>
						<div class="hidden manage-list dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'objective', action:'list', params:[surveyId:survey?.id])}"><g:message code="default.list.label" args="[message(code:'survey.objective.label',default:'Objective')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'surveySkipRule', action:'list', params:[surveyId: survey?.id])}"><g:message code="default.list.label" args="[message(code:'survey.skiprule.label',default:'Skip Rules')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'surveyValidationRule', action:'list', params:[surveyId: survey?.id])}"><g:message code="default.list.label" args="[message(code:'survey.validationrule.label',default:'Validation Rules')]" /></a>
								</li>
								<li>
							    	<a href="${createLink(controller:'survey', action:'copy', params:[surveyId: survey.id])}"><g:message code="survey.clone.label" default="Clone" /> </a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>