<r:require modules="progressbar,dropdown,explanation,survey" />

<%@ page import="org.chai.kevin.survey.SummaryPage" %> 

<div>
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SummaryPage.FACILITY_SORT}" title="${message(code: 'facility.label', default: 'Facility')}" params="${params}" defaultOrder="asc"/>
			<th><g:message code="survey.summary.objectivesubmitted.label" default="Objectives Submitted" /></th>
			<g:sortableColumn property="${SummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress', default: 'Overall progress')}" params="${params}" defaultOrder="desc"/>
			<th></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.facilities}" var="facility">
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(facility)}" />
				<g:set var="objectiveSummary" value="${summaryPage.getObjectiveSummary(facility)}" />
				<tr>
					<td class="objective-table-link" data-facility="${facility.id}">
						<a href="${createLink(controller: 'summary', action: 'objectiveTable', params: [survey: currentSurvey.id, location: facility.id])}"><g:i18n field="${facility.names}"/></a>
					</td>
					<td>${objectiveSummary.submittedObjectives}/${objectiveSummary.objectives}</td>
					<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<ul class="horizontal">
							<li>
								<a href="${createLink(controller: 'editSurvey', action: 'surveyPage', params: [survey: currentSurvey.id, location: facility.id])}">
									<g:message code="survey.summary,viewsurvey.label" default="View Survey" />
								</a>
							</li>
							<shiro:hasPermission permission="editSurvey:refresh">
								<li style="display:none;">
									<a href="${createLink(controller: 'editSurvey', action: 'refresh', params: [survey: currentSurvey.id, location: facility.id])}" onclick="return confirm('\${message(code: 'survey.summary.refresh.confirm.message', default: 'Are you sure?')}');">
										<g:message code="survey.summary.refreshsurvey.label" default="Reset Survey" />
									</a>
								</li>
							</shiro:hasPermission>
							<shiro:hasPermission permission="editSurvey:print">
								<li>
									<a href="${createLink(controller: 'editSurvey', action: 'print', params: [survey: currentSurvey.id, location: facility.id])}" target="_blank">
										<g:message code="survey.summary.printsurvey.label"	default="Print Survey" />
									</a>
								</li>
							</shiro:hasPermission>
							<shiro:hasPermission permission="editSurvey:export">
								<li>
									<a href="${createLink(controller: 'editSurvey', action: 'export', params: [survey: currentSurvey.id, location: facility.id])}">
										<g:message code="survey.summary.exportsurvey.label" default="Export Survey" />
									</a>
								</li>
							</shiro:hasPermission>
						</ul>
					</td>
				</tr>			
				<tr class="explanation-row">
					<td colspan="6">
						<div class="explanation-cell" id="explanation-${facility.id}"></div>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>