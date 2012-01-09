<%@ page import="org.chai.kevin.survey.SummaryPage" %>

<r:require modules="progressbar,dropdown,explanation,survey" />

<div id="survey-summary">
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SummaryPage.FACILITY_SORT}" title="${message(code: 'facility.label', default: 'Facility')}" params="${params}" defaultOrder="asc"/>
			<th><g:message code="survey.summary.submitted" default="Submitted" /></th>
			<g:sortableColumn property="${SummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress', default: 'Overall progress')}" params="${params}" defaultOrder="desc"/>
			<th></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.facilities}" var="facility">
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(facility)}" />
				<tr>
					<td class="section-table-link" data-objective="${currentObjective.id}" data-organisation="${facility.id}">
						<a href="${createLink(controller: 'summary', action: 'sectionTable', params: [objective: currentObjective.id, organisation: facility.id])}">
							<g:i18n field="${facility.names}"/>
						</a>
					</td>
					<td>${summaryPage.getSurveyEnteredObjective(facility)?.closed?'\u2713':''}</td>
					<td><span class="progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<a href="${createLink(controller: 'editSurvey', action: 'objectivePage', params: [objective: currentObjective.id, organisation: facility.id])}">
							<g:message code="survey.summary.viewsurvey.label" default="View Survey" />
						</a> 
						<shiro:hasPermission permission="editSurvey:export"> 
							<a href="${createLink(controller: 'editSurvey', action: 'export', params: [objective: currentObjective.id, organisation: facility.id])}">
								<g:message code="survey.summary.exportobjective.label" default="Export Survey Objective" />
							</a>
						</shiro:hasPermission>
					</td>					
				</tr>
				<tr class="explanation-row">
					<td colspan="4">
						<div class="explanation-cell" id="explanation-objective-${facility.id}-${currentObjective.id}"></div>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
	<r:script>
		$(document).ready(function() {
			$('.section-table-link').bind('click', function() {
   				var objective = $(this).data('objective');
   				var organisation = $(this).data('organisation');
   				
   				explanationClick(this, 'objective-'+organisation+'-'+objective, function(){progressBar();});
   				return false;
   			});
		});
	</r:script>
</div>