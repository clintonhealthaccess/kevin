<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q, 'planningOutput.id':params['planningOutput.id']]" title="${message(code: 'entity.name.label')}" />
			<g:sortableColumn property="prefix" params="[q:params.q, 'planningOutput.id':params['planningOutput.id']]" title="${message(code: 'planning.planningoutput.planningoutputcolumn.prefix.label')}" />
			<g:sortableColumn property="order" params="[q:params.q, 'planningOutput.id':params['planningOutput.id']]" title="${message(code: 'entity.order.label')}" />
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="planningOutputColumn">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'planningOutputColumn', action:'edit', params:[id: planningOutputColumn.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'planningOutputColumn', action:'delete', params:[id: planningOutputColumn.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${planningOutputColumn.names}"/></td>
				<td>${planningOutputColumn.prefix}</td>
				<td>${planningOutputColumn.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>