<table class="listing">
	<thead>
		<tr>
			<th/>
			<th>Question</th>
			<th>Type</th>
			<th>Organisation Unit Groups</th>
			<th>Order</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="question">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:question.getType().getTemplate(), action:'edit', params:[id: question.id])}">
								<g:message code="general.text.edit" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:question.getType().getTemplate(), action:'delete', params:[id: question.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="general.text.delete" default="Delete" />
							</a>
						</li>
					</ul>
				</td>
				<td><a title="Question" href="#" class="cluetip"
					rel="${createLink(controller:question.getType().getTemplate(), action:'getQuestionExplainer', params:[question: question.id])}"
					onclick="return false;"> ${org.chai.kevin.util.Utils.stripHtml(i18n(field: question.names).toString(),100)} </a></td>
				<td>${question.getType()}</td>
				<td>${question.groupUuidString}</td>
				<td>${question.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>

