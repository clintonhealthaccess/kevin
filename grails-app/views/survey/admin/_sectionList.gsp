<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.name.label"/></th>
			<th><g:message code="entity.locationtype.label"/></th>
			<th><g:message code="default.number.label" args="[message(code:'survey.question.label')]"/></th>
			<th><g:message code="entity.order.label"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="section"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
		    			    <a class="edit-link" href="${createLinkWithTargetURI(controller:'section', action:'edit', params:[id: section.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
		      				<a class="delete-link" href="${createLinkWithTargetURI(controller:'section', action:'delete', params:[id: section.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" />
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${section.names}" /></td>
				<td>${section.typeCodeString}</td>
				<td>${section.questions.size()}</td>
				<td>${section.order}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected manage-btn" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="hidden manage-list dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'question', action:'list',params:['section.id': section.id])}">
										<g:message code="default.list.label" args="[message(code:'survey.question.label')]" />
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
