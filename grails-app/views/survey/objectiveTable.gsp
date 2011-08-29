<%@ page import="org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'dashboard.explanation.label', default: 'Dashboard explanation')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
		<table>
			<thead>
				<th>Objective</th>
				<th>Submitted</th>
				<th>Overall progress</th>
				<th></th>
			</thead>
			<tbody>
				<g:each in="${summaryPage.objectives}" var="objective">
					<g:set var="objectiveSummary" value="${summaryPage.getObjectiveSummary(objective)}"/>
					<tr>
						<td class="section-table-link" data-objective="${objective.id}" data-organisation="${summaryPage.organisation.id}">
							<a href="${createLink(controller: 'editSurvey', action: 'sectionTable', params: [organisation: summaryPage.organisation.id, objective: objective.id])}">
								<g:i18n field="${objectiveSummary.objective.names}"/>
							</a>
						</td>
						<td>${objectiveSummary.enteredObjective?.status == ObjectiveStatus.CLOSED?'\u2713':''}</td>
						<td><span class="progress-bar">${objectiveSummary.submittedElements}/${objectiveSummary.elements}</span></td>
						<td><a href="${createLink(controller: 'editSurvey', action: 'objectivePage', params: [objective: objective.id, organisation: summaryPage.organisation.id])}">view survey</a></td>
					</tr>
					<tr>
						<td colspan="4">
							<div class="explanation-cell" id="explanation-objective-${summaryPage.organisation.id}-${objective.id}"></div>
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	
		<script type="text/javascript">
			$(document).ready(function() {
				$('.section-table-link').bind('click', function() {
    				var objective = $(this).data('objective');
    				var organisation = $(this).data('organisation');
    				
    				explanationClick(this, 'objective-'+organisation+'-'+objective, function(){progressBar();});
    				return false;
    			});
			});
		</script>
	</body>
</html>
