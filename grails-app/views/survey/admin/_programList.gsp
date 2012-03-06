<g:searchBox controller="question" action="search" params="${[survey: survey?.id]}" entityName="Survey Question"/>
<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label" default="Name"/></th>
			<th><g:message code="facility.type.label" default="Facility Groups"/></th>
			<th><g:message code="default.number.label" args="[message(code:'survey.section.label')]" default="Number of Sections"/></th>
			<th><g:message code="entity.order.label" default="Order"/></th>
			<th><g:message code="entity.list.manage.label" default="Manage"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="program">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'program', action:'edit', params:[id: program.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'program', action:'delete', params:[id: program.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="default.link.delete.label" default="Delete" />
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${program.names}" /></td>
				<td>${program.typeCodeString}</td>
				<td>${program.sections.size()}</td>
				<td>${program.order}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected dropdown manage-btn" href="#"><g:message code="entity.list.manage.label" default="Manage"/></a>
						<div class="hidden manage-list js_dropdown-list dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'section', action:'list', params:['program.id': program.id])}">
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
