<div class="main">
  <table class="listing">
  	<thead>
  		<tr>
  			<th/>
  		    <th><g:message code="task.user.label"/></th>
  			<th><g:message code="task.class.label"/></th>
  			<th><g:message code="task.senttoqueue.label"/></th>
  			<th><g:message code="task.numberoftries.label"/></th>
  			<g:sortableColumn property="status" params="[q:q]" title="${message(code: 'task.status.label')}" />
  			<g:sortableColumn property="added" params="[q:q]" title="${message(code: 'task.added.label')}" defaultOrder="desc" />
  		</tr>
  	</thead>
  	<tbody>
  		<g:each in="${entities}" status="i" var="task"> 
  			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
  				<td>
  					<ul class="horizontal">
  						<li>
  							<a class="delete-link" href="${createLinkWithTargetURI(controller:'task', action:'delete', params:[id: task.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
  								<g:message code="default.link.delete.label" />
  							</a>
  						</li>
  					</ul>
  				</td>
  				<td>${task.user.username}</td>  				
  				<td>${task.class.simpleName.toLowerCase()}</td>
  				<td>${task.sentToQueue}</td>
  				<td>${task.numberOfTries}</td>
  				<td>${task.status}</td>
  				<td><g:formatDate format="yyyy-MM-dd HH:mm" date="${task.added}"/></td>
  			</tr>
  		</g:each>
  	</tbody>
  </table>
</div>