<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
  			<g:sortableColumn property="data.code" params="[q:params.q]" title="${message(code: 'dsr.target.calculation.label')}" />
  			<g:sortableColumn property="average" params="[q:params.q]" title="${message(code: 'dsr.target.datatype.label')}" />
  			<g:sortableColumn property="${i18nField(field: 'category.names')}" params="[q:params.q]" title="${message(code: 'dsr.target.category.label')}" />
			<g:sortableColumn property="order" params="[q:params.q]" title="${message(code: 'entity.order.label')}" />
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="target">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'dsrTarget', action:'edit', params:[id: target.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'dsrTarget', action:'delete', params:[id: target.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
						</li>
						
					</ul>
				</td>
				<td>${target.code}</td>				
				<td>
					<g:i18n field="${target.names}"/>
				</td>
				<td>${target.data.code}</td>
				<td>${target.average != null && target.average ? message(code: 'average.label') : message(code: target.data.class.getSimpleName().toLowerCase()+'.label')}</td>
				<td>
					<g:i18n field="${target.category?.names}"/>
				</td>
				<td>${target.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>