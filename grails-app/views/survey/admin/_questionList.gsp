<table class="listing">
	<thead>
		<tr>
			<th>Question</th>
			<th>Type</th>
			<th>Organisation Unit Groups</th>
			<th>Order</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="question">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><a title="Question" href="#" class="cluetip"
					rel="${createLink(controller:question.getType(), action:'getQuestionExplainer', params:[question: question.id])}"
					onclick="return false;"> ${question.getString(i18n(field: question.names).toString(),100)} </a></td>
				<td>${question.getType()}</td>
				<td>${question.groupUuidString}</td>
				<td>${question.order}</td>
				<td>
				<div class="dropdown subnav-dropdown"> 
				     <a class="selected" href="#" data-type="question">Manage</a>
				<div class="hidden dropdown-list">
					<ul>
						<li>
							<a href="${createLinkWithTargetURI(controller:question.type, action:'edit', params:[id: question.id])}">
								<g:message code="general.text.edit" default="Edit" />
							</a>
						</li>
						<li>
							<a href="${createLinkWithTargetURI(controller:question.type, action:'delete', params:[id: question.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="general.text.delete" default="Delete" />
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

