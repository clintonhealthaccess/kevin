<html>
  <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <meta name="layout" content="ajax" />
      <g:set var="entityName" value="${message(code: 'dashboard.explanation.label', default: 'Dashboard explanation')}" />
      <title><g:message code="default.create.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="main">
  		<table class="listing">
  			<thead>
  				<th><g:message code="survey.objective.label" default="Objective" /></th>
				<th><g:message code="survey.summary.submitted" default="Submitted" /></th>
				<th><g:message code="survey.summary.progress" default="Overall Progress" /></th>
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
  						<td>${objectiveSummary.enteredObjective?.closed?'\u2713':''}</td>
  						<td><span class="progress-bar">${objectiveSummary.completedQuestions}/${objectiveSummary.questions}</span></td>
  						<td>
	  						<a href="${createLink(controller: 'editSurvey', action: 'objectivePage', params: [objective: objective.id, organisation: summaryPage.organisation.id])}"><g:message code="survey.summary.viewsurvey.label" default="View Survey"/></a>
	  						<shiro:hasPermission permission="editSurvey:export"> 
							<a href="${createLink(controller: 'editSurvey', action: 'export', params: [objective: objective.id, organisation: summaryPage.organisation.id])}">
							<g:message code="survey.summary.exportobjective.label" default="Export Survey Objective" /></a>
							</shiro:hasPermission>
						</td>
  					</tr>
  					<tr class="explanation-row">
  						<td colspan="4">
  							<div class="explanation-cell" id="explanation-objective-${summaryPage.organisation.id}-${objective.id}"></div>
  						</td>
  					</tr>
  				</g:each>
  			</tbody>
  		</table>
    </div>
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
	</body>
</html>
