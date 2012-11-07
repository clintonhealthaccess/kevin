<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
  			<g:sortableColumn property="${i18nField(field: 'type.names')}" params="[q:params.q]" title="${message(code: 'datalocation.type.label')}" />
  			<g:sortableColumn property="${i18nField(field: 'location.names')}" params="[q:params.q]" title="${message(code: 'datalocation.location.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'location.level.names')}" params="[q:params.q]" title="${message(code: 'locationlevel.label')}" />  		    
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="location">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'dataLocation', action:'edit', params:[id: location.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'dataLocation', action:'delete', params:[id: location.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
						</li>
					</ul>
				</td>
				<td>${location.code}</td>
				<td>
					<g:i18n field="${location.names}"/>
				</td>
				<td>
					<g:i18n field="${location.type.names}"/>
				</td>
				<td>
					<g:i18n field="${location.location.names}"/>
				</td>
				<td>
					<g:i18n field="${location.location.level.names}"/>
				</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
  								<li>
  									<a href="${createLinkWithTargetURI(controller:'data', action:'deleteValues', params:[location: location.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
  										<g:message code="data.deletevalues.label"/>
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