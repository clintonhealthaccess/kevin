<table class="listing">
	<thead>
		<tr>
		    <th>Id</th>
			<th>Survey</th>
			<th>Description</th>
			<th>Expression</th>
			<th>Number of Survey Elements</th>
			<th>Number of Questions</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="skip">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>${skip.id}</td>
				<td><g:i18n field="${skip.survey.names}" /></td>
				<td><g:i18n field="${skip.descriptions}" /></td>
				<td>${skip.expression}</td>
				<td> 
				   ${skip.skippedSurveyElements.size()}
			    </td>
				<td>
					${skip.skippedSurveyQuestions.size()}
				</td>
				<td>
					<div class="dropdown subnav-dropdown"> 
			     		<a class="selected" href="#" data-type="skip-rule">Manage</a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
					    			<a href="${createLinkWithTargetURI(controller:'surveySkipRule', action:'edit', params:[id: skip.id])}">
							    		<g:message code="general.text.edit" default="Edit" /> 
									</a>
								</li>
								<li>
					    			<a href="${createLinkWithTargetURI(controller:'surveySkipRule', action:'delete', params:[id: skip.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
						        		<g:message code="general.text.delete" default="Delete" /> 
									</a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
