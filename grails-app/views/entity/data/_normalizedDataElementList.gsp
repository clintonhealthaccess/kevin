<div class="main">
  <table class="listing">
  	<thead>
  		<tr>
  			<th/>
  			<th>Id</th>
  			<th>Names</th>
  			<th>Type</th>
  			<th>Code</th>
  		</tr>
  	</thead>
  	<tbody>
  		<g:each in="${entities}" status="i" var="normalizedDataElement">
  			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
  				<td>
  	            	<ul class="horizontal">
  			           	<li>
  			           		<a class="edit-link" href="${createLinkWithTargetURI(action:'edit', id: normalizedDataElement.id)}"><g:message code="default.link.edit.label" default="Edit" /></a>
  						</li>
  			           	<li>
  			           		<a class="delete-link" href="${createLinkWithTargetURI(controller:'normalizedDataElement', action:'delete', params:[id:normalizedDataElement.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
  						</li>
  		           	</ul>
  				</td>
  				<td>${normalizedDataElement.id}</td>
  				<td><g:i18n field="${normalizedDataElement.names}"/></td>
  				<td><g:toHtml value="${normalizedDataElement.type.getDisplayedValue(2, 2)}"/></td>
  				<td>${normalizedDataElement.code}</td>
  			</tr>
  		</g:each>
  	</tbody>
  </table>
</div>