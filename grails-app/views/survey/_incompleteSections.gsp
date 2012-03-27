<g:set value="${surveyPage.getIncompleteSections(surveyPage.program)}" var="incompleteSections"/>
<g:if test="${!incompleteSections.isEmpty()}">
	<div>
		<g:message code="survey.program.incomplete.text" />:
		<ul>
			<g:each in="${incompleteSections}" var="section">
				<li>
					<a href="${createLink(controller:'editSurvey', action:'sectionPage', params:[section:section.id, location: surveyPage.location.id])}">
						<g:i18n field="${section.names}"/>
					</a>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>