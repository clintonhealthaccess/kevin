<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="survey.question.label" default="Question"/></th>
			<th><g:message code="type.label" default="Type"/></th>
			<th><g:message code="facility.type.label" default="Facility Groups"/></th>
			<th><g:message code="entity.order.label" default="Order"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="question">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:question.getType().getTemplate(), action:'edit', params:[id: question.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:question.getType().getTemplate(), action:'delete', params:[id: question.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="default.link.delete.label" default="Delete" />
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

