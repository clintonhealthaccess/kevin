<div class="admin-entity-list">
	<div id="ValidationMessages">	
	<div class="float-left">
			<h5>Validation Message List</h5>
		</div>
		<div class="float-right">
			<a id="add-validation-message-link" class="flow-add" href="${createLink(controller:'validationMessage', action:'create')}">New Validation Message</a>
		</div>
		<div class="admin-table-list">
			<table>
			<g:if test="${!validationMessages.isEmpty()}">
					<tr class="admin-table-header">
					   <g:sortableColumn property="id" title="${message(code: 'validationMessage.id.label', default: 'Id')}" />
						<th>Message</th>
						<th>Number of Validation Rules Attached</th>
						<th>Manage</th>
					</tr>
						<g:each in="${validationMessages}" status="i" var="validationMessage">
							<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								<td>${validationMessage.id}</td>
								<td><g:i18n field="${validationMessage.messages}" /></td>
								<td>${validationMessage.validationRules.size()}</td>
								<td>
							<div class="dropdown"> 
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
					</g:if>
					<g:else>
						<tr>
							<td colspan="5">No Validation Message available <a id="new-validation-message-link" class="flow-add"
				href="${createLink(controller:'createValidationMessage', action:'create')}">Add
								Validation Message</a></td>
						</tr> 
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${validationMessageCount}" />
		</div>
	</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
