<div class="main">
	<table class="listing">
		<thead>
			<tr>
				<th/>
				<th>Username</th>
				<th>Email</th>
				<th>Permissions</th>
				<th>Roles</th>
				<g:sortableColumn property="confirmed" title="${message(code: 'user.confirmed.label', default: 'Confirmed')}" />
				<g:sortableColumn property="active" title="${message(code: 'user.active.label', default: 'Active')}" />
				<th><g:message code="entity.list.manage.label" default="Manage"/></th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${entities}" status="i" var="user">
  				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
  					<td>
  	            		<ul class="horizontal">
  			           		<li>
  			           			<a class="edit-link" href="${createLinkWithTargetURI(action:'edit', id: user.id)}"><g:message code="default.link.edit.label" default="Edit" /></a>
  							</li>
  			           		<li>
  			           			<a class="delete-link" href="${createLinkWithTargetURI(controller:'user', action:'delete', params:[id:user.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
  							</li>
  		           		</ul>
  					</td>
  					<td>${user.username}</td>
	  				<td>${user.email}</td>
	  				<td>${user.permissionString}</td>
	  				<td>${user.roles}</td>
	  				<td>${user.confirmed?'\u2713':''}</td>
	  				<td>${user.active?'\u2713':''}</td>
	  				<td>
	  					<g:if test="${user.canActivate()}">
	  						<a href="${createLinkWithTargetURI(controller:'auth', action:'activate', params:[id:user.id])}">activate</a>
	  					</g:if>
	  				</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>