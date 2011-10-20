<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'dashboard.explanation.label', default: 'Dashboard Explanation')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
		<table>
			<thead>
				<th><g:message code="general.text.section" default="Section"/></th>
				<th><g:message code="general.text.overallprogress" default="Overall Progress"/></th>
				<th></th>
			</thead>
			<tbody>
				<g:each in="${summaryPage.sections}" var="section">
					<g:set var="sectionSummary" value="${summaryPage.getSectionSummary(section)}"/>
					<tr>
						<td><g:i18n field="${sectionSummary.section.names}"/></td>
						<td><span class="progress-bar">${sectionSummary.completedQuestions}/${sectionSummary.questions}</span></td>
						<td><a href="${createLink(controller: 'editSurvey', action: 'sectionPage', params: [section: section.id, organisation: summaryPage.organisation.id])}"><g:message code="survey.viewsurvey.label" default="View Survey"/></a></td>
					</tr>
				</g:each>
			</tbody>
		</table>
	
	</body>
</html>
