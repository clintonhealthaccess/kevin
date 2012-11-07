<%@page import="org.chai.kevin.util.Utils"%>
<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q, 'section.id': params['section.id']]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q, 'section.id': params['section.id']]" title="${message(code: 'survey.question.label')}" />
  			<th><g:message code="survey.question.type.label"/></th>
			<g:sortableColumn property="typeCodeString" params="[q:params.q, 'section.id': params['section.id']]" title="${message(code: 'entity.datalocationtype.label')}" />
			<th><g:message code="survey.label" /></th>
			<th><g:message code="survey.program.label" /></th>
			<th><g:message code="survey.section.label" /></th>
			<g:sortableColumn property="order" params="[q:params.q, 'section.id': params['section.id']]" title="${message(code: 'entity.order.label')}" />
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
				<td>${question.code}</td>
				<td><a title="Question" href="#" class="cluetip"
					rel="${createLink(controller:'question', action:'getDescription', params:[question: question.id])}"
					onclick="return false;"><g:stripHtml field="${i18n(field: question.names)}" chars="100"/></a></td>
				<td>${question.getType()}</td>
				<td><g:prettyList entities="${question.typeCodeString}" /></td>
				<td>${question.section.program.survey.code}</td>
				<td>${question.section.program.code}</td>
				<td>${question.section.code}</td>
				<td>${question.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>

