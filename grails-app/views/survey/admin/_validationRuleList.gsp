<table class="listing">
	<thead>
		<tr>
			<th/>
			<th>Data Element</th>
			<th>Prefix</th>
			<th>Expression</th>
			<th>Allow Outlier</th>
			<th>Organisation Unit Groups</th>
			<th>Message</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="validationRule">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'surveyValidationRule', action:'edit', params:[id: validationRule.id])}">
								<g:message code="general.text.edit" default="Edit" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'surveyValidationRule', action:'delete', params:[id: validationRule.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
								<g:message code="general.text.delete" default="Delete" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${validationRule.surveyElement.dataElement?.names}" /></td>
				<td>${validationRule.prefix}</td>
				<td>${validationRule.expression}</td>
				<td> 
				    <g:if test="${validationRule.allowOutlier==true}">
				    &radic;
				    </g:if>
			        <g:else>
			        X
			        </g:else>
			    </td>
			    <td>${validationRule.groupUuidString}</td>
				<td><g:i18n field="${validationRule.messages}" /></td>
			</tr>
		</g:each>
	</tbody>
</table>
