<r:require modules="progressbar,dropdown,explanation,survey" />

<div id="survey-summary">
	<table class="listing">
		<thead>
			<th><g:message code="facility.label" default="Facility" /></th>
			<th><g:message code="survey.summary.submitted" default="Submitted" /></th>
			<th><g:message code="survey.summary.progress" default="Overall Progress" /></th>
			<th></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.getObjectiveFacilities()}" var="facility">
				<g:set var="objectiveSummary" value="${summaryPage.getObjectiveSummary(facility)}" />
				<tr>
					<td class="section-table-link" data-objective="${objective.id}" data-organisation="${facility.id}">
					<a
						href="${createLink(controller: 'editSurvey', action: 'sectionTable', params: [objective: objective.id, organisation: facility.id])}">
							${facility.name} </a></td>
					<td>${objectiveSummary.enteredObjective?.closed?'\u2713':''}</td>
					<td><span class="progress-bar">${objectiveSummary.completedQuestions}/${objectiveSummary.questions}</span></td>
					<td><a
						href="${createLink(controller: 'editSurvey', action: 'objectivePage', params: [objective: objective.id, organisation: facility.id])}">
						<g:message code="survey.summary.viewsurvey.label" default="View Survey" /></a> 
						<shiro:hasPermission permission="editSurvey:export"> 
						<a href="${createLink(controller: 'editSurvey', action: 'export', params: [objective: objective.id, organisation: facility.id])}">
						<g:message code="survey.summary.exportobjective.label" default="Export Survey Objective" /></a>
						</shiro:hasPermission></td>					
				</tr>
				<tr class="explanation-row">
					<td colspan="4">
						<div class="explanation-cell" id="explanation-objective-${facility.id}-${objective.id}"></div>
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