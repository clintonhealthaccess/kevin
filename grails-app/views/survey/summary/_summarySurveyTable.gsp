<r:require modules="progressbar,dropdown,explanation,survey" />

<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %> 

<div>
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SurveySummaryPage.LOCATION_SORT}" title="${message(code: 'location.label')}" params="${params}" defaultOrder="asc"/>
			<th><g:message code="survey.summary.programsubmitted.label" /></th>
			<g:sortableColumn property="${SurveySummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress')}" params="${params}" defaultOrder="desc"/>
			<th><g:message code="entity.list.manage.label"/></th>
		</thead>
		<tbody>
			<g:set var="surveysClosed" value="${true}"/>
			<g:each in="${summaryPage.locations}" var="location">
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(location)}" />
				<g:set var="programSummary" value="${summaryPage.getProgramSummary(location)}" />
				<g:set var="surveyClosed" value="${programSummary.submittedPrograms == programSummary.programs}" />
				<tr>
					<td class="program-table-link" data-location="${location.id}">
						<a href="${createLink(controller: 'surveySummary', action: 'programTable', params: [survey: currentSurvey.id, location: location.id])}"><g:i18n field="${location.names}"/></a>
					</td>
					<td>${programSummary.submittedPrograms}/${programSummary.programs}</td>
					<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<div class="js_dropdown dropdown"> 
							<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
							<div class="dropdown-list js_dropdown-list">
								<ul>
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
									<shiro:hasPermission permission="surveyExport:export">
										<li>
											<a href="${createLink(controller: 'surveyExport', action: 'export', params: [survey: currentSurvey.id, location: location.id])}">
												<g:message code="survey.summary.exportsurvey.label" />
											</a>
										</li>
									</shiro:hasPermission>
									<shiro:hasPermission permission="surveySummary:submitAll">
										<g:if test="${!surveyClosed}">
											<g:set var="surveysClosed" value="${false}"/>
											<li>
												<a href="${createLink(controller: 'surveySummary', action: 'submitAll', params: [survey: currentSurvey?.id, location: currentLocation.id, submitLocation: location.id])}">
													<g:message code="survey.summary.submitsurvey.label" />
												</a>
											</li>														
										</g:if>
									</shiro:hasPermission>
								</ul>
							</div>
						</div>
					</td>
				</tr>			
				<tr class="explanation-row">
					<td colspan="6">
						<div class="explanation-cell" id="explanation-${location.id}"></div>
					</td>
				</tr>
			</g:each>
			<shiro:hasPermission permission="surveySummary:submitAll">
				<g:if test="${!submitSkipLevels.contains(currentLocation.level) && !surveysClosed}">
					<tr>
						<td></td>
						<td></td>
						<td></td>
						<td><a
							href="${createLink(controller: 'surveySummary', action: 'submitAll', params: params << [survey: currentSurvey?.id, submitLocation: currentLocation.id])}">
								<g:message code="survey.summary.submitallsurvey.label" />
						</a></td>
					</tr>
				</g:if>
			</shiro:hasPermission>			
		</tbody>
	</table>
</div>