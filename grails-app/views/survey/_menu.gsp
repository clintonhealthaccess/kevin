<r:require module="foldable"/>

<ul>
	<g:each in="${surveyPage.getObjectives()}" var="objective">
		<g:set var="enteredObjective" value="${surveyPage.enteredObjectives[objective]}"/>
		
		<li id="objective-${objective.id}" class="foldable ${surveyPage.objective?.id == objective.id?'current':''}">
			<a class="foldable-toggle" href="#">(toggle)</a>
		
			<a class="item ${surveyPage.objective?.id == objective.id?'opened':''}" href="${createLink(controller:'editSurvey', action:'objectivePage', params:[location: surveyPage.location.id, objective:objective.id])}">
				<span><g:i18n field="${objective.names}" /></span>
				<span class="item-status">
					<span class="objective-status-complete objective-status ${enteredObjective.displayedStatus!='complete'?'hidden':''}"></span>
					<span class="objective-status-invalid  objective-status ${enteredObjective.displayedStatus!='invalid'?'hidden':''}"></span>
					<span class="objective-status-incomplete objective-status ${enteredObjective.displayedStatus!='incomplete'?'hidden':''}"></span>
					<span class="objective-status-closed objective-status ${enteredObjective.displayedStatus!='closed'?'hidden':''}"></span>
				</span>
			</a>
			<ul class="survey-section">
				<g:each in="${surveyPage.getSections(objective)}" var="section">
					<g:set var="enteredSection" value="${surveyPage.enteredSections[section]}"/>

					<li id="section-${section.id}">
						<a class="item ${surveyPage.section?.id == section.id?'opened':''}" href="${createLink(controller:'editSurvey', action:'sectionPage', params:[location: surveyPage.location.id, section:section.id])}">
							<span><g:i18n field="${section.names}" /></span>
							<span class="item-status">
								<span class="section-status-complete section-status ${enteredSection.displayedStatus!='complete'?'hidden':''}"></span>
								<span class="section-status-invalid section-status ${enteredSection.displayedStatus!='invalid'?'hidden':''}"></span>
								<span class="section-status-incomplete section-status ${enteredSection.displayedStatus!='incomplete'?'hidden':''}"></span>
							</span>
						</a>
					</li>
				</g:each>
			</ul>
		</li>
	</g:each>
</ul>
