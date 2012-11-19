<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
  			<g:sortableColumn property="${i18nField(field: 'parent.names')}" params="[q:params.q]" title="${message(code: 'reports.program.parent.label')}" />
			<g:sortableColumn property="order" params="[q:params.q]" title="${message(code: 'entity.order.label')}" />  		    
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="program">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'reportProgram', action:'edit', params:[id: program.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'reportProgram', action:'delete', params:[id: program.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
						</li>
						
					</ul>
				</td>
				<td>${program.code}</td>
				<td>
					<g:i18n field="${program.names}"/>
				</td>
				<td>
					<g:i18n field="${program.parent?.names}"/>
				</td>
				<td>${program.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>