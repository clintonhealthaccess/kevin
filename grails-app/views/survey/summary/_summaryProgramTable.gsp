<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %>

<r:require modules="progressbar,dropdown,explanation,survey" />

<div>
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SurveySummaryPage.LOCATION_SORT}" title="${message(code: 'location.label')}" params="${params}" defaultOrder="asc"/>
			<th><g:message code="survey.summary.submitted" /></th>
			<g:sortableColumn property="${SurveySummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress')}" params="${params}" defaultOrder="desc"/>
			<th><g:message code="entity.list.manage.label"/></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.locations}" var="location">
				<g:set var="programClosed" value="${summaryPage.getSurveyEnteredProgram(location)?.closed}" />
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(location)}" />
				<tr>
					<td class="section-table-link" data-program="${currentProgram.id}" data-location="${location.id}">
						<a href="${createLink(controller: 'surveySummary', action: 'sectionTable', params: [program: currentProgram.id, location: location.id])}">
							<g:i18n field="${location.names}"/>
						</a>
					</td>
					<td>${programClosed?'\u2713':''}</td>
					<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<div class="js_dropdown dropdown"> 
							<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
							<div class="dropdown-list js_dropdown-list">
								<ul>
									<li>
										<a href="${createLink(controller: 'editSurvey', action: 'programPage', params: [program: currentProgram.id, location: location.id])}">
											<g:message code="survey.summary.viewprogram.label" />
										</a>
									</li> 
									<shiro:hasPermission permission="surveyExport:export">
										<li> 
											<a href="${createLink(controller: 'surveyExport', action: 'export', params: [program: currentProgram.id, location: location.id])}">
												<g:message code="survey.summary.exportprogram.label" />
											</a>
										</li>
									</shiro:hasPermission>
									<shiro:hasPermission permission="surveySummary:submitAll">
										<li> 
											<a href="${createLink(controller: 'surveySummary', action: 'submitAll', params: [program: currentProgram.id, location: currentLocation.id, submitLocation: location.id])}">
												<g:message code="survey.summary.submitprogram.label" />
											</a>
										</li>
									</shiro:hasPermission>
								</ul>
							</div>
						</div>
					</td>					
				</tr>
				<tr class="explanation-row">
					<td colspan="4">
						<div class="explanation-cell" id="explanation-program-${location.id}-${currentProgram.id}"></div>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
	<r:script>
		$(document).ready(function() {
			$('.section-table-link').bind('click', function() {
   				var program = $(this).data('program');
   				var location = $(this).data('location');
   				
   				explanationClick(this, 'program-'+location+'-'+program, function(){progressBar();});
   				return false;
   			});
		});
	</r:script>
</div>