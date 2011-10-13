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
				    			    <a href="${createLinkWithTargetURI(controller:'section', action:'edit', params:[id: section.id])}">
										<g:message code="general.text.edit" default="Edit" />
									</a>
								</li>
								<li>
				      				<a href="${createLinkWithTargetURI(controller:'section', action:'delete', params:[id: section.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
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
