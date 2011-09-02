<div class="survey-admin-entity-list">
	<div id="surveys">	
	<div class="float-left">
			<h5>Survey List</h5>
		</div>
		<div class="float-right">
			<a id="add-survey-link" class="flow-add" href="${createLink(controller:'createSurvey', action:'create')}">New Survey</a>
		</div>
		<div class="admin-table-list">
			<table>
			<g:if test="${!surveys.isEmpty()}">
					<tr class="admin-table-header">
						<th>Name</th>
						<th>Description</th>
						<th>Period</th>
						<th>Number Objectives</th>
						<th>Manage</th>
					</tr>
						<g:each in="${surveys}" status="i" var="survey">
							<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								<td><g:i18n field="${survey.names}" /></td>
								<td><g:i18n field="${survey.descriptions}" /></td>
								<td>[${survey.period.startDate} - ${survey.period.startDate}]</td>
								<td>${survey.objectives.size()}</td>
								<td>
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="survey">Manage</a>
							<div class="hidden dropdown-list">
								<ul>
								<li>
								<a href="${createLink(controller:'admin', action:'objective',params:[surveyId: survey?.id])}">Objectives</a>
								</li>
								<li>
								   <a href="${createLink(controller:'admin', action:'skiprules',params:[surveyId: survey?.id])}">Skip Rules</a>
								</li>
									<li class="edit-survey-link">
									<g:link controller="createSurvey" action="edit" id="${survey.id}" class="flow-edit">
									    <g:message code="general.text.edit" default="Edit" /> 
									</g:link>
									</li>
									<li class="delete-survey-link">
								    <g:link controller="createSurvey" action="delete" id="${survey.id}" class="flow-delete">
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
							<td colspan="5">No survey available <a id="new-survey-link" class="flow-add"
				href="${createLink(controller:'createSurvey', action:'create')}">Add
								Survey</a></td>
						</tr> 
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${surveyCount}" />
		</div>
	</div>
	<div class="clear"></div>
</div>

