<g:searchBox controller="question" action="search" params="${[surveyId: survey?.id]}" entityName="Survey Question"/>
<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label" default="Name"/></th>
			<th><g:message code="entity.description.label" default="Description"/></th>
			<th><g:message code="facility.type.label" default="Facility Groups"/></th>
			<th><g:message code="default.number.label" args="[message(code:'survey.section.label')]" default="Number of Sections"/></th>
			<th><g:message code="entity.order.label" default="Order"/></th>
			<th><g:message code="entity.list.manage.label" default="Manage"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="objective">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'objective', action:'edit', params:[id: objective.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'objective', action:'delete', params:[id: objective.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="default.link.delete.label" default="Delete" />
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${objective.names}" /></td>
				<td><g:i18n field="${objective.descriptions}" /></td>
				<td>${objective.groupUuidString}</td>
				<td>${objective.sections.size()}</td>
				<td>${objective.order}</td>
				<td>
					<div class="dropdown subnav-dropdown"> 
						<a class="selected" href="#" data-type="objective"><g:message code="entity.list.manage.label" default="Manage"/></a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'section', action:'list', params:[surveyId:survey?.id,objectiveId: objective.id])}">
										<g:message code="default.list.label" args="[message(code:'survey.section.label',default:'Section')]" />
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
