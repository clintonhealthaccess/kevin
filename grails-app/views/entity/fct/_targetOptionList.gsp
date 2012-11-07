<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
  			<g:sortableColumn property="data.code" params="[q:params.q]" title="${message(code: 'fct.targetoption.sum.label')}" />
  			<g:sortableColumn property="${i18nField(field: 'target.names')}" params="[q:params.q]" title="${message(code: 'fct.targetoption.target.label')}" />
			<g:sortableColumn property="order" params="[q:params.q]" title="${message(code: 'entity.order.label')}" />
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="targetOption">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'fctTargetOption', action:'edit', params:[id: targetOption.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'fctTargetOption', action:'delete', params:[id: targetOption.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
						</li>
						
					</ul>
				</td>
				<td>${targetOption.code}</td>
				<td>
					<g:i18n field="${targetOption.names}"/>
				</td>
				<td>${targetOption.data.code}</td>
				<td>
					<g:i18n field="${targetOption.target.names}"/>
				</td>
				<td>${targetOption.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>