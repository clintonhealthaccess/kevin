<r:require modules="progressbar,dropdown,explanation,survey" />

<div id="survey-summary">
	<table class="listing">
		<thead>
			<th><g:message code="facility.label" default="Facility" /></th>
			<th><g:message code="survey.summary.objectivesubmitted.label" default="Objectives Submitted" /></th>
			<th><g:message code="survey.summary.progress" default="Overall progress" /></th>
			<th></th>
			<th><shiro:hasPermission permission="editSurvey:export">
					<a href="${createLink(controller: 'editSurvey', action: 'export', params: [survey: survey.id, organisation: organisation.id])}">
						<g:message code="survey.summary.exportall.label" default="Export All" /></a>
				</shiro:hasPermission>
			</th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.facilities}" var="facility">
				<g:set var="surveySummary" value="${summaryPage.getSurveySummary(facility)}" />
				<tr>
					<td class="objective-table-link" data-facility="${facility.id}">
						<a
						href="${createLink(controller: 'editSurvey', action: 'objectiveTable', params: [survey: survey.id, organisation: facility.id])}">${facility.name}</a>
					</td>
					<td>${surveySummary.submittedObjectives}/${surveySummary.objectives}</td>
					<td><span class="progress-bar">${surveySummary.completedQuestions}/${surveySummary.questions}</span></td>
					<td>
						<ul class="horizontal">
							<li><a
								href="${createLink(controller: 'editSurvey', action: 'surveyPage', params: [survey: survey.id, organisation: facility.id])}"><g:message
										code="survey.summary,viewsurvey.label" default="View Survey" /></a></li>
							<shiro:hasPermission permission="editSurvey:refresh">
								<li><a
									href="${createLink(controller: 'editSurvey', action: 'refresh', params: [survey: survey.id, organisation: facility.id])}"><g:message
											code="survey.summary.refreshsurvey.label"
											default="Refresh Survey" /></a></li>
							</shiro:hasPermission>
							<shiro:hasPermission permission="editSurvey:print">
								<li><a
									href="${createLink(controller: 'editSurvey', action: 'print', params: [survey: survey.id, organisation: facility.id])}"
									target="_blank"><g:message
											code="survey.summary.printsurvey.label"
											default="Print Survey" /></a></li>
							</shiro:hasPermission>
							<shiro:hasPermission permission="editSurvey:export">
								<li><a href="${createLink(controller: 'editSurvey', action: 'export', params: [survey: survey.id, organisation: facility.id])}">
								<g:message code="survey.summary.exportsurvey.label" default="Export Survey" /></a></li>
							</shiro:hasPermission>
						</ul>
					</td>
					<td></td>
				</tr>			
				<tr class="explanation-row">
					<td colspan="5">
						<div class="explanation-cell" id="explanation-${facility.id}"></div>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>