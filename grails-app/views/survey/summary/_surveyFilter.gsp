<%@ page import="org.chai.kevin.survey.SummaryPage" %>

<div class="filter">
	<span class="bold"><g:message code="survey.label" default="Survey" />:</span>
	<span class="js_dropdown dropdown">
		<a class="survey selected" href="#"> 
			<g:if test="${currentSection != null}">
				<g:i18n field="${currentSection.names}" />
			</g:if> 
			<g:elseif test="${currentObjective != null}">
				<g:i18n field="${currentObjective.names}" />
			</g:elseif> 
			<g:elseif test="${currentSurvey != null}">
				<g:i18n field="${currentSurvey.names}" />
			</g:elseif> 
			<g:else>
				<g:message code="default.select.label" args="[message(code:'survey.label')]" default="Select a survey" />
			</g:else>
		</a>
		<div id="survey-menu" class="hidden dropdown-list js_dropdown-list">
			<ul>
				<g:each in="${surveys}" var="survey">
					<li id="survey-${survey.id}" class="foldable ${currentSurvey?.id==survey.id?'current':''}">
						<a class="foldable-toggle" href="#">(toggle)</a> 
						<a class="item ${currentSurvey?.id == survey.id? 'opened':''}" href="${createLink(controller: 'summary', action:'summaryPage', params:[location: currentLocation?.id, survey: survey.id, sort: SummaryPage.PROGRESS_SORT, order:'desc'])}">
							<g:i18n field="${survey.names}" />
						</a>
						<ul class="survey-objective">
							<g:each in="${survey.getObjectives()}" var="objective">
								<li id="objective-${objective.id}" class="foldable ${currentObjective?.id==objective.id?'current':''}">
									<a class="foldable-toggle" href="#">(toggle)</a> 
									<a class="item ${currentObjective?.id == objective.id?'opened':''}" href="${createLink(controller:'summary', action:'summaryPage', params:[location: currentLocation?.id, objective: objective.id, sort: SummaryPage.PROGRESS_SORT, order:'desc'])}">
										<span><g:i18n field="${objective.names}" /></span>
									</a>
									<ul class="survey-section">
										<g:each in="${objective.getSections()}" var="section">
											<li id="section-${section.id}" class="foldable ${currentSection?.id==section.id?'current':''}">
												<a class="item ${currentSection?.id == section.id?'opened':''}" href="${createLink(controller:'summary', action:'summaryPage', params:[location: currentLocation?.id, section: section.id, sort: SummaryPage.PROGRESS_SORT, order:'desc'])}">
													<span><g:i18n field="${section.names}" /></span>
												</a>
											</li>
										</g:each>
									</ul>
								</li>
							</g:each>
						</ul>
					</li>
				</g:each>
			</ul>
		</div>

	</span>
</div>