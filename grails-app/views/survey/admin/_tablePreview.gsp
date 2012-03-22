<div class="white-box entity-list">
	<g:i18n field="${question.names}" />
	<table class="question-table" id="question-table-${question.id}">
		<thead>
			<tr>
				<th><g:i18n field="${question.tableNames}" /></th>
			    <g:set var="j" value="${0}"/>
				<g:each in="${columns}" var="column">
					<g:set var="j" value="${j++}"/>
					<th class="${columns.size()!=j?'question-tab-title':''}">
						<g:i18n field="${column.names}" />			
						<div> 
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'tableColumn', action:'edit', id: column.id)}">
								<g:message code="default.link.edit.label" />
							</a>&nbsp;
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'tableColumn', action:'delete', id: column.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" />
							</a>
						</div>
					</th>
				</g:each>
			</tr>
		</thead>
		<tbody>
			<g:each in="${rows}" var="row" status="i">
				<tr class="${i%2==0?'odd':'even'}">
					<td>
					<g:i18n field="${row.names}" />
						<div> 
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'tableRow', action:'edit', id: row.id)}"> 
								<g:message code="default.link.edit.label" />
							</a>&nbsp;
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'tableRow', action:'delete', id: row.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" />
							</a>
						</div>
					</td>
					<g:each in="${columns}" var="column">
						<g:set var="surveyElement" value="${row.surveyElements[column]}"/>
						<g:if test="${surveyElement != null}">
						<g:set var="dataElement" value="${surveyElement?.dataElement}"/>
							<td class="element-${surveyElement?.id} element" data-element="${surveyElement?.id}">
								<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}" model="[
									type: dataElement.type, 
									suffix: '',
									element: surveyElement,
									enteredValue: null,
									readonly: readonly
								]" />
								
								<a href="${createLink(controller:'surveyValidationRule', action:'list', params:['formElement.id': surveyElement?.id])}">
									<g:message code="survey.viewvalidationrule.label"/>
								</a>
							</td>
						</g:if>
					</g:each>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>
