<div class="survey-admin-entity-list">
	<div id="surveys">
		<div class="float-right">
			<a id="add-survey-link" class="flow-add" href="${createLink(controller:'createSurvey', action:'create')}">New Survey</a>
		</div>
		<div id="survey-admin-table-list">
			<table>
			<g:if test="${!surveys.isEmpty()}">
					<tr class="table-header">
					    <th>Order</th>
						<th>ID</th>
						<th>Name</th>
						<th>Description</th>
						<th>Period</th>
						<th>Number Objectives</th>
						<th><g:message code="general.text.edit" default="Edit" /></th>
						<th><g:message code="general.text.delete" default="Delete" /></th>
					</tr>
						<g:each in="${surveys}" status="i" var="survey">
							<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							    <td>${survey?.order}</td>
								<td>${survey.id}</td>
								<td><g:i18n field="${survey.names}" /></td>
								<td><g:i18n field="${survey.descriptions}" /></td>
								<td>[${survey.period.startDate} - ${survey.period.startDate}]</td>
								<td><a href="${createLink(controller:'admin', action:'objective',params:[survey: survey.id])}">${survey.objectives.size()}</a>
								</td>
								<td class="edit-survey-link">
									<g:link controller="createSurvey" action="edit" id="${survey.id}" class="flow-edit">
									    <g:message code="general.text.edit" default="Edit" /> 
									</g:link>
								</td>
								<td class="delete-survey-link">
								    <g:link controller="createSurvey" action="delete" id="${survey.id}" class="flow-delete">
								        <g:message code="general.text.delete" default="Delete" /> 
								    </g:link>
								</td>
							</tr>
						</g:each>
					</g:if>
					<g:else>
						<tr>
							<td colspan="7">No survey available</td>
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

