<table>
	<thead>
		<tr>
		   <g:sortableColumn property="id" title="${message(code: 'validationMessage.id.label', default: 'Id')}" />
			<th>Message</th>
			<th>Number of Validation Rules Attached</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="validationMessage">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>${validationMessage.id}</td>
				<td><g:i18n field="${validationMessage.messages}" /></td>
				<td>${validationMessage.validationRules.size()}</td>
				<td>
			<div class="dropdown white-dropdown"> 
			     <a class="selected" href="#" data-type="validation-message">Manage</a>
				<div class="hidden dropdown-list">
					<ul>
						<li class="edit-validation-message-link">
						<g:link controller="validationMessage" action="edit" id="${validationMessage.id}" class="flow-edit">
						    <g:message code="general.text.edit" default="Edit" /> 
						</g:link>
						</li>
						<li class="delete-validation-message-link">
					    <g:link controller="validationMessage" action="delete" id="${validationMessage.id}" class="flow-delete">
					        <g:message code="general.text.delete" default="Delete" /> 
					    </g:link>
						</li>
					</ul>
				</div>
			</div> 		
			</td>
			</tr>
		</g:each>
	</tbody>
</table>
