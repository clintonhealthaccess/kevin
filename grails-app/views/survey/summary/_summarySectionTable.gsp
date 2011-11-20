<r:require modules="progressbar,dropdown,explanation,survey" />

<%@ page import="org.chai.kevin.survey.SummaryPage" %>

<div class="right">
	<shiro:hasPermission permission="editSurvey:export">
		<a href="${createLink(controller: 'editSurvey', action: 'export', params: [section: currentSection.id, organisation: organisation.id])}">
			<g:message code="survey.summary.exportall.label" default="Export All" />
		</a>
	</shiro:hasPermission>
</div>

<div id="survey-summary">
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SummaryPage.FACILITY_SORT}" title="${message(code: 'facility.label', default: 'Facility')}" params="${params}" defaultOrder="asc"/>
			<g:sortableColumn property="${SummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress', default: 'Overall progress')}" params="${params}" defaultOrder="desc"/>
			<th></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.facilities}" var="facility">
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(facility)}" />
				<tr>
					<td>${facility.name}</td>
					<td><span class="progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<a href="${createLink(controller: 'editSurvey', action: 'sectionPage', params: [section: currentSection.id, organisation: facility.id])}">
							<g:message code="survey.viewsurvey.label" default="View Survey" />
						</a>
						<shiro:hasPermission permission="editSurvey:export"> 
							<a href="${createLink(controller: 'editSurvey', action: 'export', params: [section: currentSection.id, organisation: facility.id])}">
								<g:message code="survey.summary.exportsection.label" default="Export Survey Section" />
							</a>
						</shiro:hasPermission>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>
