<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:q]" title="${message(code: 'entity.code.label')}" />
			<th><g:message code="entity.name.label"/></th>
			<th><g:message code="entity.description.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="source"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'source', action:'edit', params:[id: source.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'source', action:'delete', params:[id: source.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" />
							</a>
						</li>
					</ul>
				</td>
				<td>${source.code}</td>				
				<td><g:i18n field="${source.names}" /></td>
				<td><g:i18n field="${source.descriptions}" /></td>
			</tr>
		</g:each>
	</tbody>
</table>
