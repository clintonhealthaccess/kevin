<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label" default="Name"/></th>
			<th><g:message code="facility.type.label" default="Facility Groups"/></th>
			<th><g:message code="default.number.label" args="[message(code:'survey.question.label')]" default="Number of Questions"/></th>
			<th><g:message code="entity.order.label" default="Order"/></th>
			<th><g:message code="entity.list.manage.label" default="Manage"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="section"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
		    			    <a class="edit-link" href="${createLinkWithTargetURI(controller:'section', action:'edit', params:[id: section.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
		      				<a class="delete-link" href="${createLinkWithTargetURI(controller:'section', action:'delete', params:[id: section.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="default.link.delete.label" default="Delete" />
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${section.names}" /></td>
				<td>${section.typeCodeString}</td>
				<td>${section.questions.size()}</td>
				<td>${section.order}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected" href="#" data-type="section"><g:message code="entity.list.manage.label" default="Manage"/></a>
						<div class="hidden dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'question', action:'list',params:[surveyId:objective.survey?.id,objectiveId: objective?.id,sectionId: section.id])}">
										<g:message code="default.list.label" args="[message(code:'survey.question.label',default:'Questions')]" />
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
