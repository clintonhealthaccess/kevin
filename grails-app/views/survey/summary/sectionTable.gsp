<html>
  <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <meta name="layout" content="ajax" />
  </head>
  <body>
    <div class="main">
  		<table class="listing">
  			<thead>
  				<th><g:message code="survey.section.label"/></th>
  				<th><g:message code="survey.summary.progress"/></th>
  				<th><g:message code="entity.list.manage.label"/></th>
  			</thead>
  			<tbody>
  				<g:each in="${summaryPage.sections}" var="section">
  					<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(section)}"/>
  					<tr>
  						<td><g:i18n field="${section.names}"/></td>
  						<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
  						<td>
  							<div class="js_dropdown dropdown"> 
								<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
								<div class="dropdown-list js_dropdown-list">
									<ul>
										<li>
  											<a href="${createLink(controller: 'editSurvey', action: 'sectionPage', params: [section: section.id, location: location.id])}"><g:message code="survey.summary.viewsurvey.label"/></a>
  										</li>
										<shiro:hasPermission permission="surveyExport:export"> 
											<li>
												<a href="${createLink(controller: 'surveyExport', action: 'export', params: [section: section.id, location: location.id])}">
												<g:message code="survey.summary.exportsection.label" /></a>
											</li>
										</shiro:hasPermission>
									</ul>
								</div>
							</div>
						</td>
  					</tr>
  				</g:each>
  			</tbody>
  		</table>
		</div>	
	</body>
</html>
