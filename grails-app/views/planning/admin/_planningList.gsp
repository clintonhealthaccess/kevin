<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label"/></th>
			<th><g:message code="entity.datalocationtype.label"/></th>
			<th><g:message code="planning.active.label"/></th>
			<th><g:message code="period.label"/></th>
			<th><g:message code="default.number.label" args="[message(code:'planning.planningtype.label')]"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="planning">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'planning', action:'edit', params:[id: planning.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'planning', action:'delete', params:[id: planning.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${planning.names}" /></td>
				<td>${planning.typeCodeString}</td>
				<td>${planning?.active?'\u2713':''}</td>
				<td>[${planning.period.startDate} - ${planning.period.startDate}]</td>
				<td>${planning.planningTypes.size()}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected manage-btn" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="hidden manage-list dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'planningType', action:'list', params:['planning.id':planning.id])}"><g:message code="default.list.label" args="[message(code:'planning.planningtype.label')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'planningOutput', action:'list', params:['planning.id':planning.id])}"><g:message code="default.list.label" args="[message(code:'planning.planningoutput.label')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'planningSkipRule', action:'list', params:['planning.id': planning?.id])}"><g:message code="default.list.label" args="[message(code:'planning.skiprule.label')]" /></a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>