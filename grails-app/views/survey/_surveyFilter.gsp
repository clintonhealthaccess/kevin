<r:require module="foldable"/>

<g:if test="${section != null}">
	<g:set var="currentSelection" value="${section}" />	
	<g:set var="currentSurveySection" value="${section}" />
	<g:set var="currentSurveyObjective" value="${section.objective}" />
	<g:set var="currentSurvey" value="${section.objective.survey}" />
</g:if>
<g:elseif test="${objective != null}">
	<g:set var="currentSelection" value="${objective}" />	
	<g:set var="currentSurveyObjective" value="${objective}" />
	<g:set var="currentSurvey" value="${objective.survey}" />
</g:elseif>
<g:elseif test="${survey != null}">
	<g:set var="currentSelection" value="${survey}" />
	<g:set var="currentSurvey" value="${survey}" />
</g:elseif>

<div class="filter">
	<span class="bold"><g:message code="survey.label"
			default="Survey" />:</span> 
	<span class="dropdown subnav-dropdown">
		<a class="selected" href="#"> 
			<g:if test="${currentSelection != null}">
				<g:i18n field="${currentSelection.names}" />
			</g:if> 			 
			<g:else>
				<g:message code="default.select.label"
					args="[message(code:'survey.label')]" default="Select a survey" />
			</g:else>
	</a>
		<div id="survey-menu" class="hidden dropdown-list">		
			<ul>
				<g:each in="${surveys}" var="survey">
					<li id="survey-${survey.id}"
						class="foldable ${currentSurvey?.id == survey.id? 'current':''}"><a
						class="foldable-toggle" href="#">(toggle)</a> <a
						class="item ${currentSurvey?.id == survey.id? 'opened':''}"
						href="${createLink(controller: 'editSurvey', action:'summaryPage', params:[organisation: organisation?.id, survey: survey.id])}">
							<g:i18n field="${survey.names}" />
					</a>
						<ul class="survey-objective">
							<g:each in="${survey.getObjectives()}" var="objective">

								<li id="objective-${objective.id}"
									class="foldable ${currentSurveyObjective?.id == objective.id?'current':''}">
									<a class="foldable-toggle" href="#">(toggle)</a> <a
									class="item ${currentSurveyObjective?.id == objective.id?'opened':''}"
									href="${createLink(controller:'editSurvey', action:'summaryPage', params:[organisation: organisation?.id, objective: objective.id])}">
										<span><g:i18n field="${objective.names}" /></span>
								</a>
									<ul class="survey-section">
										<g:each in="${objective.getSections()}" var="section">

											<li id="section-${section.id}"><a
												class="item ${currentSurveySection?.id == section.id?'opened':''}"
												href="${createLink(controller:'editSurvey', action:'summaryPage', params:[organisation: organisation?.id, section: section.id])}">
													<span><g:i18n field="${section.names}" /></span>
											</a></li>
										</g:each>
									</ul>
								</li>
							</g:each>
						</ul></li>
				</g:each>
			</ul>
		</div>
	</span>
</div>