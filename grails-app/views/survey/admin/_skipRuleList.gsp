<div class="admin-entity-list">
	<div id="skip-rules">	
	<div class="float-left">
			<h5>Skip Rule List</h5>
		</div>
		<div class="float-right">
			<a id="add-skip-rule-link" class="flow-add" href="${createLink(controller:'surveySkipRule', action:'create',params:[surveyId: survey.id])}">New Skip Rule</a>
		</div>
		<div class="admin-table-list">
			<table>
			<g:if test="${!skipRules.isEmpty()}">
					<tr class="admin-table-header">
					    <th>Id</th>
						<th>Survey</th>
						<th>Description</th>
						<th>Expression</th>
						<th>Number of Survey Elements</th>
						<th>Number of Questions</th>
						<th>Manage</th>
					</tr>
						<g:each in="${skipRules}" status="i" var="skip">
							<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								<td>${skip.id}</td>
								<td><g:i18n field="${skip.survey.names}" /></td>
								<td><g:i18n field="${skip.descriptions}" /></td>
								<td>${skip.expression}</td>
								<td> 
								   ${skip.skippedSurveyElements.size()}
							    </td>
								<td>${skip.skippedSurveyQuestions.size()}</td>
								<td>
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="skip-rule">Manage</a>
							<div class="hidden dropdown-list">
								<ul>
									<li class="edit-skip-rule-link">
									<g:link controller="surveySkipRule" action="edit" id="${skip.id}" class="flow-edit">
									    <g:message code="general.text.edit" default="Edit" /> 
									</g:link>
									</li>
									<li class="delete-skip-rule-link">
								    <g:link controller="surveySkipRule" action="delete" id="${skip.id}" class="flow-delete">
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
							<td colspan="5">No Skip Rule available <a id="new-skip-rule-link" class="flow-add"
				href="${createLink(controller:'surveySkipRule', action:'create',params:[surveyId: survey.id])}">Add
								Skip Rule</a></td>
						</tr> 
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${skipRuleCount}" />
		</div>
	</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>

