<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
  			<g:sortableColumn property="${i18nField(field: 'level.names')}" params="[q:params.q]" title="${message(code: 'location.level.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'parent.names')}" params="[q:params.q]" title="${message(code: 'location.parent.label')}" />
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="location">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'location', action:'edit', params:[id: location.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'location', action:'delete', params:[id: location.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
						</li>
						
					</ul>
				</td>
				<td>${location.code}</td>
				<td>
					<g:i18n field="${location.names}"/>
				</td>
				<td>
					<g:i18n field="${location.level.names}"/>
				</td>
				<td>
					<g:i18n field="${location.parent?.names}"/>
				</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
  									<a href="${createLink(controller:'dataLocation', action:'list', params:[location:location.id])}">
  										<g:message code="default.list.label" args="[message(code:'datalocation.label')]" />
  									</a>
  								</li>
  								<li>
  									<a href="${createLink(controller:'location', action:'list', params:[parent:location.id])}">
  										<g:message code="default.list.label" args="[message(code:'location.label')]" />
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