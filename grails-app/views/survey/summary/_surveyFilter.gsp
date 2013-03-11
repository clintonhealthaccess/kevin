<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %>
<div class="left">
	<%
		surveyLinkParams = [:]
		surveyLinkParams.putAll linkParams
	%>
	<span class="js_dropdown dropdown">
		<a class="survey js_dropdown-link nice-button with-highlight" href="#"> 
			<g:if test="${currentSection != null}">
				<g:i18n field="${currentSection.names}" />
			</g:if> 
			<g:elseif test="${currentProgram != null}">
				<g:i18n field="${currentProgram.names}" />
			</g:elseif> 
			<g:elseif test="${currentSurvey != null}">
				<g:i18n field="${currentSurvey.names}" />
			</g:elseif>
			<g:else>
				<g:message code="default.select.label" args="[message(code:'survey.label')]" />
			</g:else>
		</a>
		<div class="dropdown-list js_dropdown-list push-top-10">
			<ul>
				<g:each in="${surveys}" var="survey">
				
					<li class="js_foldable foldable ${currentSurvey?.id==survey.id?'current':''}">
						<a class="js_foldable-toggle foldable-toggle" href="#">(toggle)</a>
						<a class="item ${currentSurvey?.id == survey.id? 'opened':''}" href="${createLinkExcludeParams(controller: 'surveySummary', action:'summaryPage', params: surveyLinkParams << [survey: survey.id], exclude: ['program', 'section'])}">
							<g:i18n field="${survey.names}" />
						</a>
						<ul class="js_foldable-container foldable-container">
							<g:each in="${survey.getPrograms()}" var="program">
							
								<li class="js_foldable foldable ${currentProgram?.id==program.id?'current':''}">
									<a class="js_foldable-toggle foldable-toggle" href="#">(toggle)</a> 
									<a class="item ${currentProgram?.id == program.id?'opened':''}" 
										href="${createLinkExcludeParams(controller:'surveySummary', action:'summaryPage', params: surveyLinkParams << [survey: survey.id, program: program.id], exclude:['section'])}">
										<g:i18n field="${program.names}" />
									</a>
									<ul class="js_foldable-container foldable-container">
										<g:each in="${program.getSections()}" var="section">
											<li class="js_foldable foldable ${currentSection?.id==section.id?'current':''}">
												<a class="item ${currentSection?.id == section.id?'opened':''}" 
													href="${createLink(controller:'surveySummary', action:'summaryPage', params: surveyLinkParams << [survey: survey.id, program: program.id, section: section.id])}">
													<g:i18n field="${section.names}" />
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