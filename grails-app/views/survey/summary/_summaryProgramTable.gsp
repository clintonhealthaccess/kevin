<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %>

<r:require modules="progressbar,dropdown,explanation,survey" />

<div>
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SurveySummaryPage.LOCATION_SORT}" title="${message(code: 'location.label')}" params="${params}" defaultOrder="asc"/>
			<th><g:message code="survey.summary.submitted" /></th>
			<g:sortableColumn property="${SurveySummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress')}" params="${params}" defaultOrder="desc"/>
			<th></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.locations}" var="location">
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(location)}" />
				<tr>
					<td class="section-table-link" data-program="${currentProgram.id}" data-location="${location.id}">
						<a href="${createLink(controller: 'surveySummary', action: 'sectionTable', params: [program: currentProgram.id, location: location.id])}">
							<g:i18n field="${location.names}"/>
						</a>
					</td>
					<td>${summaryPage.getSurveyEnteredProgram(location)?.closed?'\u2713':''}</td>
					<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<a href="${createLink(controller: 'editSurvey', action: 'programPage', params: [program: currentProgram.id, location: location.id])}">
							<g:message code="survey.summary.viewsurvey.label" />
						</a> 
						<shiro:hasPermission permission="editSurvey:export"> 
							<a href="${createLink(controller: 'editSurvey', action: 'export', params: [program: currentProgram.id, location: location.id])}">
								<g:message code="survey.summary.exportprogram.label" />
							</a>
						</shiro:hasPermission>
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