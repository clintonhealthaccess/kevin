<div class="admin-entity-list">
	<div id="ValidationRules">	
	<div class="float-left">
			<h5>Validation Rule List</h5>
		</div>
		<div class="float-right">
			<a id="add-validation-rule-link" class="flow-add" href="${createLink(controller:'surveyValidationRule', action:'create',params:[surveyId: surveyElement.id])}">New Validation Rule</a>
		</div>
		<div class="admin-table-list">
			<table>
			<g:if test="${!validationRules.isEmpty()}">
					<tr class="admin-table-header">
					    <th>Id</th>
						<th>Data Element</th>
						<th>Expression</th>
						<th>Allow Outlier</th>
						<th>Validation Message</th>
						<th>Manage</th>
					</tr>
						<g:each in="${validationRules}" status="i" var="validationRule">
							<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								<td>${validationRule.id}</td>
								<td><g:i18n field="${validationRule.surveyElement.dataElement?.names}" /></td>
								<td>${validationRule.expression}</td>
								<td> 
								    <g:if test="${validationRule.allowOutlier==true}">
								    &radic;
								    </g:if>
							        <g:else>
							        X
							        </g:else>
							    </td>
								<td><g:i18n field="${validationRule.validationMessage.messages}" /></td>
								<td>
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="validation-rule">Manage</a>
							<div class="hidden dropdown-list">
								<ul>
									<li class="edit-validation-rule-link">
									<g:link controller="surveyValidationRule" action="edit" id="${validationRule.id}" class="flow-edit">
									    <g:message code="general.text.edit" default="Edit" /> 
									</g:link>
									</li>
									<li class="delete-validation-rule-link">
								    <g:link controller="surveyValidationRule" action="delete" id="${validationRule.id}" class="flow-delete">
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
							<td colspan="5">No Validation Rule available <a id="new-validation-rule-link" class="flow-add"
				href="${createLink(controller:'surveyValidationRule', action:'create',params:[surveyId: surveyElement.id])}">Add
								Validation Rule</a></td>
						</tr> 
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${validationRuleCount}" />
		</div>
	</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>

