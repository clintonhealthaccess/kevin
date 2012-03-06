<r:require module="foldable"/>

<ul>
	<g:each in="${surveyPage.getPrograms()}" var="program">
		<g:set var="enteredProgram" value="${surveyPage.enteredPrograms[program]}"/>
		
		<li id="program-${program.id}" class="foldable ${surveyPage.program?.id == program.id?'current':''}">
			<a class="foldable-toggle" href="#">(toggle)</a>
		
			<a class="item ${surveyPage.program?.id == program.id?'opened':''}" href="${createLink(controller:'editSurvey', action:'programPage', params:[location: surveyPage.location.id, program:program.id])}">
				<span><g:i18n field="${program.names}" /></span>
				
				<span class="item-status">
					<span class="program-status-complete program-status ${enteredProgram.displayedStatus!='complete'?'hidden':''}"></span>
					<span class="program-status-invalid  program-status ${enteredProgram.displayedStatus!='invalid'?'hidden':''}"></span>
					<span class="program-status-incomplete program-status ${enteredProgram.displayedStatus!='incomplete'?'hidden':''}"></span>
					<span class="program-status-closed program-status ${enteredProgram.displayedStatus!='closed'?'hidden':''}"></span>
				</span>
			</a>
			<ul class="survey-section">
				<g:each in="${surveyPage.getSections(program)}" var="section">
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
