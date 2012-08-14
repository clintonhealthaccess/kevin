<r:require modules="progressbar,dropdown,explanation,survey" />

<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %>

<div>
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SurveySummaryPage.LOCATION_SORT}" title="${message(code: 'location.label')}" params="${params}" defaultOrder="asc"/>
			<g:sortableColumn property="${SurveySummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress')}" params="${params}" defaultOrder="desc"/>
			<th><g:message code="entity.list.manage.label"/></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.locations}" var="location">
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(location)}" />
				<tr>
					<td><g:i18n field="${location.names}"/></td>
					<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller: 'editSurvey', action: 'sectionPage', params: [section: currentSection.id, location: location.id])}">
										<g:message code="survey.summary.viewsurvey.label" />
									</a>
								</li>
								<shiro:hasPermission permission="surveyExport:export">
									<li> 
										<a href="${createLink(controller: 'surveyExport', action: 'export', params: [section: currentSection.id, location: location.id])}">
											<g:message code="survey.summary.exportsection.label" />
										</a>
									</li>
								</shiro:hasPermission>
							</ul>
						</div>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>
