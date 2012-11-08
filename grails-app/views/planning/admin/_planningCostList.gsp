<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q, 'planningType.id':params['planningType.id']]" title="${message(code: 'entity.name.label')}" />
			<g:sortableColumn property="type" params="[q:params.q, 'planningType.id':params['planningType.id']]" title="${message(code: 'planning.planningcost.type.label')}" />
			<g:sortableColumn property="dataElement" params="[q:params.q, 'planningType.id':params['planningType.id']]" title="${message(code: 'planning.planningcost.dataelement.label')}" />
			<g:sortableColumn property="hideIfZero" params="[q:params.q, 'planningType.id':params['planningType.id']]" title="${message(code: 'planning.planningcost.hideifzero.label')}" />
			<g:sortableColumn property="order" params="[q:params.q, 'planningType.id':params['planningType.id']]" title="${message(code: 'entity.order.label')}" />
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="planningCost">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'planningCost', action:'edit', params:[id: planningCost.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'planningCost', action:'delete', params:[id: planningCost.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${planningCost.names}"/></td>
				<td>${planningCost.type}</td>
				<td><g:i18n field="${planningCost.dataElement.names}"/>[${planningCost.dataElement.id}]</td>
				<td>${planningCost?.hideIfZero?'\u2713':''}</td>
				<td>${planningCost.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>