<html>
  <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <meta name="layout" content="ajax" />
  </head>
  <body>
    <div class="main">
  		<table class="listing">
  			<thead>
  				<th><g:message code="survey.program.label" /></th>
				<th><g:message code="survey.summary.submitted" /></th>
				<th><g:message code="survey.summary.progress" /></th>
  				<th></th>
  			</thead>
  			<tbody>
  				<g:each in="${summaryPage.programs}" var="program">
  					<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(program)}"/>
  					<tr>
  						<td class="section-table-link" data-program="${program.id}" data-location="${location.id}">
  							<a href="${createLink(controller: 'surveySummary', action: 'sectionTable', params: [location: location.id, program: program.id])}">
  								<g:i18n field="${program.names}"/>
  							</a>
  						</td>
  						<td>${summaryPage.getSurveyEnteredProgram(program)?.closed?'\u2713':''}</td>
  						<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
  						<td>
  							<a href="${createLink(controller: 'editSurvey', action: 'programPage', params: [program: program.id, location: location.id])}"><g:message code="survey.summary.viewsurvey.label"/></a>
  							<shiro:hasPermission permission="editSurvey:export"> 
								<a href="${createLink(controller: 'editSurvey', action: 'export', params: [program: program.id, location: location.id])}">
									<g:message code="survey.summary.exportprogram.label" />
								</a>
							</shiro:hasPermission>
						</td>
  					</tr>
  					<tr class="explanation-row">
  						<td colspan="4">
  							<div class="explanation-cell" id="explanation-program-${location.id}-${program.id}"></div>
  						</td>
  					</tr>
  				</g:each>
  			</tbody>
  		</table>
    </div>
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
	</body>
</html>
