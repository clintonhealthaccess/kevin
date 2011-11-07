<r:require modules="progressbar,dropdown,explanation,survey" />

<div id="survey-summary">
	<table class="listing">
		<thead>
			<th><g:message code="survey.section.label" default="Section" /></th>
			<th><g:message code="survey.summary.progress" default="Overall Progress" /></th>
			<th></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.getSectionFacilities()}" var="facility">
				<g:set var="sectionSummary" value="${summaryPage.getSectionSummary(facility)}" />
				<tr>
					<td>${facility.name}</td>
					<td><span class="progress-bar">${sectionSummary.completedQuestions}/${sectionSummary.questions}</span></td>
					<td><a
						href="${createLink(controller: 'editSurvey', action: 'sectionPage', params: [section: section.id, organisation: facility.id])}"><g:message
								code="survey.viewsurvey.label" default="View Survey" /></a>
						<shiro:hasPermission permission="editSurvey:export"> 
						<a href="${createLink(controller: 'editSurvey', action: 'export', params: [section: section.id, organisation: facility.id])}">
						<g:message code="survey.summary.exportsection.label" default="Export Survey Section" /></a>
						</shiro:hasPermission></td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>
