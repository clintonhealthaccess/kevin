<r:require modules="progressbar,dropdown,explanation,survey" />

<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %>

<div>
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SurveySummaryPage.FACILITY_SORT}" title="${message(code: 'facility.label', default: 'Facility')}" params="${params}" defaultOrder="asc"/>
			<g:sortableColumn property="${SurveySummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress', default: 'Overall progress')}" params="${params}" defaultOrder="desc"/>
			<th></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.facilities}" var="facility">
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(facility)}" />
				<tr>
					<td><g:i18n field="${facility.names}"/></td>
					<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<a href="${createLink(controller: 'editSurvey', action: 'sectionPage', params: [section: currentSection.id, location: facility.id])}">
							<g:message code="survey.viewsurvey.label" default="View Survey" />
						</a>
						<shiro:hasPermission permission="editSurvey:export"> 
							<a href="${createLink(controller: 'editSurvey', action: 'export', params: [section: currentSection.id, location: facility.id])}">
								<g:message code="survey.summary.exportsection.label" default="Export Survey Section" />
							</a>
						</shiro:hasPermission>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>
