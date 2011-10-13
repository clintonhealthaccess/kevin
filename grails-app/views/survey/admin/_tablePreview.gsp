<div class="white-box entity-list">
	<g:i18n field="${question.names}" />
	<table class="question-table" id="question-table-${question.id}">
		<thead>
			<tr>
				<th><g:i18n field="${question.tableNames}" /></th>
			    <g:set var="j" value="${0}"/>
				<g:each in="${question.getColumns()}" var="column">
					<g:set var="j" value="${j++}"/>
					<th class="${question.getColumns().size()!=j?'question-tab-title':''}">
						<g:i18n field="${column.names}" />			
						<div> 
							<a href="${createLinkWithTargetURI(controller:'tableColumn', action:'edit', id: column.id)}">
								<g:message code="general.text.edit" default="Edit" />
							</a>&nbsp;
							<a href="${createLinkWithTargetURI(controller:'tableColumn', action:'delete', id: column.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="general.text.delete" default="Delete" />
							</a>
						</div>
					</th>
				</g:each>
			</tr>
		</thead>
		<tbody>
			<g:each in="${question.getRows()}" var="row" status="i">
				<tr class="${i%2==0?'odd':'even'}">
					<td>
					<g:i18n field="${row.names}" />
						<div> 
							<a href="${createLinkWithTargetURI(controller:'tableRow', action:'edit', id: row.id)}"> 
								<g:message code="general.text.edit" default="Edit" />
							</a>&nbsp;
							<a href="${createLinkWithTargetURI(controller:'tableRow', action:'delete', id: row.id)}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="general.text.delete" default="Delete" />
							</a>
						</div>
					</td>
					<g:each in="${question.getColumns()}" var="column">
						<g:set var="surveyElement" value="${row.surveyElements[column]}"/>
						<g:if test="${surveyElement != null}">
						<g:set var="dataElement" value="${surveyElement?.dataElement}"/>
							<td class="element-${surveyElement?.id} element" data-element="${surveyElement?.id}">
								<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}" model="[
									value: null,
									lastValue: null,
									type: dataElement.type, 
									suffix: '',
									surveyElement: surveyElement,
									enteredValue: null,
									readonly: readonly
								]" />
								
								<a href="${createLink(controller:'surveyValidationRule', action:'list', params:[elementId: surveyElement?.id])}">
									View Validation Rules
								</a>
							</td>
						</g:if>
					</g:each>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>
