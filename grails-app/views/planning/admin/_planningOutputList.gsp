<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label"/></th>
			<th><g:message code="planning.planningoutput.dataelement.label"/></th>
			<th><g:message code="planning.planningoutput.fixedheader.label"/></th>
			<th><g:message code="default.number.label" args="[message(code:'planning.planningoutput.planningoutputcolumn.label')]"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="planningOutput">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'planningOutput', action:'edit', params:[id: planningOutput.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'planningOutput', action:'delete', params:[id: planningOutput.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${planningOutput.names}"/></td>
				<td><g:i18n field="${planningOutput.dataElement.names}"/></td>
				<td>${planningOutput.fixedHeader}</td>
				<td>${planningOutput.columns.size()}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected manage-btn" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="hidden manage-list dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'planningOutputColumn', action:'list', params:['planningOutput.id':planningOutput.id])}"><g:message code="default.list.label" args="[message(code:'planning.planningoutput.planningoutputcolumn.label')]" /></a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>