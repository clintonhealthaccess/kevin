<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.code.label"/></th>
			<th><g:message code="entity.name.label"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="dataLocationType">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'dataLocationType', action:'edit', params:[id: dataLocationType.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'dataLocationType', action:'delete', params:[id: dataLocationType.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
						</li>
						
					</ul>
				</td>
				<td>${dataLocationType.code}</td>
				<td>
					<g:i18n field="${dataLocationType.names}"/>
				</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
  									<a href="${createLink(controller:'dataLocation', action:'list', params:[type:dataLocationType.id])}">
  										<g:message code="default.list.label" args="[message(code:'datalocation.label')]" />
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