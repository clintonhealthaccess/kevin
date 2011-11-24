<div class="main">
  <table class="listing">
  	<thead>
  		<tr>
  			<th/>
  		    <g:sortableColumn property="id" title="${message(code: 'calculation.id.label', default: 'Id')}" />
  		    <th><g:message code="calculation.type.label" default="Type"/></th>
  			<th><g:message code="entity.name.label" default="Name"/></th>
  			<g:sortableColumn property="code" title="${message(code: 'calculation.code.label', default: 'Code')}" />
  			<th><g:message code="calculation.expression.label" default="Expression"/></th>
  		</tr>
  	</thead>
  	<tbody>
  		<g:each in="${entities}" status="i" var="calculation"> 
  			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
  				<td>
  					<ul class="horizontal">
  						<li>
  							<a class="edit-link" href="${createLinkWithTargetURI(controller:calculation.class.simpleName.toLowerCase(), action:'edit', params:[id: calculation.id])}">
  								<g:message code="default.link.edit.label" default="Edit" />
  							</a>
  						</li>
  						<li>
  							<a class="delete-link" href="${createLinkWithTargetURI(controller:calculation.class.simpleName.toLowerCase(), action:'delete', params:[id: calculation.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
  								<g:message code="default.link.delete.label" default="Delete" />
  							</a>
  						</li>
  					</ul>
  				</td>
  				<td>${calculation.id}</td>
  				<td><g:message code="${calculation.class.simpleName}.label" default="${calculation.class.simpleName}"/></td>
  				<td data-data="${calculation.id}"><g:i18n field="${calculation.names}" /></td>
  				<td>${calculation.code}</td>
  				<td>${calculation.expression}</td>
  			</tr>
  		</g:each>
  	</tbody>
  </table>
</div>