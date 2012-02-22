<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label" default="Name"/></th>
			<th><g:message code="survey.active.label" default="Active"/></th>
			<th><g:message code="period.label" default="Period"/></th>
			<th><g:message code="default.number.label" args="[message(code:'planning.planningType.label', default:"Planning types")]"/></th>
			<th><g:message code="entity.list.manage.label" default="Manage"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="planning">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'planning', action:'edit', params:[id: planning.id])}">
								<g:message code="default.link.edit.label" default="Edit" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'planning', action:'delete', params:[id: planning.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="default.link.delete.label" default="Delete" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${planning.names}" /></td>
				<td>${planning?.active?'\u2713':''}</td>
				<td>[${planning.period.startDate} - ${planning.period.startDate}]</td>
				<td>${planning.planningTypes.size()}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected manage-btn" href="#"><g:message code="entity.list.manage.label" default="Manage"/></a>
						<div class="hidden manage-list dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'planningType', action:'list', params:['planning.id':planning.id])}"><g:message code="default.list.label" args="[message(code:'planning.planningType.label',default:'Planning Type')]" /></a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>