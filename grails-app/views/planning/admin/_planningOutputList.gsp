<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q, 'planning.id':params['planning.id']]" title="${message(code: 'entity.name.label')}" />
			<g:sortableColumn property="dataElement" params="[q:params.q, 'planning.id':params['planning.id']]" title="${message(code: 'planning.planningoutput.dataelement.label')}" />
			<g:sortableColumn property="fixedHeader" params="[q:params.q, 'planning.id':params['planning.id']]" title="${message(code: 'planning.planningoutput.fixedheader.label')}" />
			<th><g:message code="default.number.label" args="[message(code:'planning.planningoutput.planningoutputcolumn.label')]"/></th>
			<g:sortableColumn property="order" params="[q:params.q, 'planning.id':params['planning.id']]" title="${message(code: 'entity.order.label')}" />
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
				<td>${planningOutput.order}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
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