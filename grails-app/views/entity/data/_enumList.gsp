<table class="listing">
	<thead>
		<tr>
			<th/>
			<th>Name</th>
			<th>Description</th>
			<g:sortableColumn property="code" title="${message(code: 'enum.code.label', default: 'Code')}" />
			<th>Number of Option</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="enumation"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'enum', action:'edit', params:[id: enumation.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'enum', action:'delete', params:[id: enumation.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="default.link.delete.label" default="Delete" />
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${enumation.names}" /></td>
				<td><g:i18n field="${enumation.descriptions}" /></td>
				<td>${enumation.code}</td>
				<td>${enumation.enumOptions.size()}</td>
				<td>
				<div class="js_dropdown dropdown"> 
					<a class="selected manage-btn" href="#">Manage</a>
					<div class="hidden js_dropdown-list manage-list dropdown-list">
						<ul>
							<li>
								<a href="${createLink(controller:'enumOption', action:'list', params:['enume.id': enumation.id])}">
									<g:message code="default.list.label" args="[message(code:'enum.enumoption.label')]" />
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
