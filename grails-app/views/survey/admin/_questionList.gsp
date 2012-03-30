<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="survey.question.label"/></th>
			<th><g:message code="survey.question.type.label"/></th>
			<th><g:message code="entity.datalocationtype.label"/></th>
			<th><g:message code="entity.order.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="question">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:question.getType().getTemplate(), action:'edit', params:[id: question.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:question.getType().getTemplate(), action:'delete', params:[id: question.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" />
							</a>
						</li>
					</ul>
				</td>
				<td><a title="Question" href="#" class="cluetip"
					rel="${createLink(controller:question.getType().getTemplate(), action:'getDescription', params:[question: question.id])}"
					onclick="return false;"><g:stripHtml field="${question.names}" chars="100"/></a></td>
				<td>${question.getType()}</td>
				<td>${question.typeCodeString}</td>
				<td>${question.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>

