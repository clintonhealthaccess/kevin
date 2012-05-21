<r:require modules="progressbar,dropdown,explanation,survey" />

<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %> 

<div>
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SurveySummaryPage.LOCATION_SORT}" title="${message(code: 'location.label')}" params="${params}" defaultOrder="asc"/>
			<th><g:message code="survey.summary.programsubmitted.label" /></th>
			<g:sortableColumn property="${SurveySummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress')}" params="${params}" defaultOrder="desc"/>
			<th>				
			</th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.locations}" var="location">
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(location)}" />
				<g:set var="programSummary" value="${summaryPage.getProgramSummary(location)}" />
				<tr>
					<td class="program-table-link" data-location="${location.id}">
						<a href="${createLink(controller: 'surveySummary', action: 'programTable', params: [survey: currentSurvey.id, location: location.id])}"><g:i18n field="${location.names}"/></a>
					</td>
					<td>${programSummary.submittedPrograms}/${programSummary.programs}</td>
					<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<ul class="horizontal">
							<li>
								<a href="${createLink(controller: 'editSurvey', action: 'surveyPage', params: [survey: currentSurvey.id, location: location.id])}">
									<g:message code="survey.summary.viewsurvey.label" />
								</a>
							</li>
							<shiro:hasPermission permission="editSurvey:refresh">
								<li style="display:none;">
									<a href="${createLink(controller: 'editSurvey', action: 'refresh', params: [survey: currentSurvey.id, location: location.id])}" onclick="return confirm('\${message(code: 'survey.summary.refresh.confirm.message')}');">
										<g:message code="survey.summary.refreshsurvey.label" />
									</a>
								</li>
							</shiro:hasPermission>
							<shiro:hasPermission permission="editSurvey:print">
								<li>
									<a href="${createLink(controller: 'editSurvey', action: 'print', params: [survey: currentSurvey.id, location: location.id])}" target="_blank">
										<g:message code="survey.summary.printsurvey.label"	default="Print Survey" />
									</a>
								</li>
							</shiro:hasPermission>
							<shiro:hasPermission permission="editSurvey:export">
								<li>
									<a href="${createLink(controller: 'editSurvey', action: 'export', params: [survey: currentSurvey.id, location: location.id])}">
										<g:message code="survey.summary.exportsurvey.label" />
									</a>
								</li>
							</shiro:hasPermission>
							<shiro:hasPermission permission="editSurvey:submitAll">
								<g:if test="${programSummary.submittedPrograms < programSummary.programs}">
										<li>
											<a href="${createLink(controller: 'surveySummary', action: 'submitAll', params: [survey: currentSurvey.id, location: currentLocation.id, submitLocation: location.id])}">
												<g:message code="survey.summary.submitsurvey.label" />
											</a>
										</li>														
								</g:if>
							</shiro:hasPermission>
						</ul>
					</td>
				</tr>			
				<tr class="explanation-row">
					<td colspan="6">
						<div class="explanation-cell" id="explanation-${location.id}"></div>
					</td>
				</tr>
			</g:each>
			<shiro:hasPermission permission="editSurvey:submitAll">
				<g:if test="${!skipLevels.contains(currentLocation.level)}">
					<tr>
						<td></td>
						<td></td>
						<td></td>
						<td><a
							href="${createLink(controller: 'surveySummary', action: 'submitAll', params: params << [survey: currentSurvey.id, submitLocation: currentLocation.id])}">
								<g:message code="survey.summary.submitall.label" />
						</a></td>
					</tr>
				</g:if>
			</shiro:hasPermission>			
		</tbody>
	</table>
</div>