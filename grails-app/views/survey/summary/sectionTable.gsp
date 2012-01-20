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
  				<th><g:message code="survey.section.label" default="Section"/></th>
  				<th><g:message code="survey.summary.progress" default="Overall Progress"/></th>
  				<th></th>
  			</thead>
  			<tbody>
  				<g:each in="${summaryPage.sections}" var="section">
  					<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(section)}"/>
  					<tr>
  						<td><g:i18n field="${section.names}"/></td>
  						<td><span class="progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
  						<td>
  							<a href="${createLink(controller: 'editSurvey', action: 'sectionPage', params: [section: section.id, location: location.id])}"><g:message code="survey.viewsurvey.label" default="View Survey"/></a>
  							<shiro:hasPermission permission="editSurvey:export"> 
								<a href="${createLink(controller: 'editSurvey', action: 'export', params: [section: section.id, location: location.id])}">
								<g:message code="survey.summary.exportsection.label" default="Export Survey Section" /></a>
							</shiro:hasPermission>
						</td>
  					</tr>
  				</g:each>
  			</tbody>
  		</table>
		</div>	
	</body>
</html>
