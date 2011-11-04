<div class="main">
  <table class="listing">
  	<thead>
  		<tr>
  			<th/>
  			<th>Names</th>
  			<th>Type</th>
  			<th>Code</th>
  		</tr>
  	</thead>
  	<tbody>
  		<g:each in="${entities}" status="i" var="expression">
  			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
  				<td>
  	            	<ul class="horizontal">
  			           	<li>
  			           		<a class="edit-link" href="${createLinkWithTargetURI(action:'edit', id: expression.id)}"><g:message code="default.link.edit.label" default="Edit" /></a>
  						</li>
  			           	<li>
  			           		<a class="delete-link" href="${createLinkWithTargetURI(controller:'expression', action:'delete', params:[id:expression.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
  						</li>
  		           	</ul>
  				</td>
  				<td><g:i18n field="${expression.names}"/></td>
  				<td><g:toHtml value="${expression.type.getDisplayedValue(2, 2)}"/></td>
  				<td>${expression.code}</td>
  			</tr>
  		</g:each>
  	</tbody>
  </table>
</div>