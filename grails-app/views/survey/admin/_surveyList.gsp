<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label"/></th>
			<th><g:message code="survey.active.label"/></th>
			<th><g:message code="entity.description.label"/></th>
			<th><g:message code="period.label"/></th>
			<th><g:message code="default.number.label" args="[message(code:'survey.program.label')]"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="survey">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'survey', action:'edit', params:[id: survey.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'survey', action:'delete', params:[id: survey.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${survey.names}" /></td>
				<td>${survey?.active?'\u2713':''}</td>
				<td><g:i18n field="${survey.descriptions}" /></td>
				<td>[${survey.period.startDate} - ${survey.period.startDate}]</td>
				<td>${survey.programs.size()}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected manage-btn" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="hidden manage-list dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'program', action:'list', params:['survey.id':survey?.id])}"><g:message code="default.list.label" args="[message(code:'survey.program.label')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'surveySkipRule', action:'list', params:['survey.id': survey?.id])}"><g:message code="default.list.label" args="[message(code:'survey.skiprule.label')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'surveyValidationRule', action:'list', params:['survey.id': survey?.id])}"><g:message code="default.list.label" args="[message(code:'survey.validationrule.label')]" /></a>
								</li>
								<li>
							    	<a href="${createLink(controller:'survey', action:'copy', params:[survey: survey.id])}"><g:message code="survey.clone.label" /> </a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>