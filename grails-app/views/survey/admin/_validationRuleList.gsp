<table>
	<thead>
		<tr>
		    <th>Id</th>
			<th>Data Element</th>
			<th>Expression</th>
			<th>Allow Outlier</th>
			<th>Validation Message</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="validationRule">
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
			<div class="dropdown white-dropdown"> 
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
	</tbody>
</table>
