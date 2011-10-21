<div class="filter">
	<span class="bold"><g:message code="survey.label" default="Survey"/>:</span>
	<span class="dropdown subnav-dropdown">
		<a class="selected" href="#">
			<g:if test="${survey != null}">
				<g:i18n field="${survey.names}" />
			</g:if>
			<g:else>
			    <g:message code="default.select.label" args="[message(code:'survey.label')]" default="Select a survey"/>
			</g:else>
		</a>
		<div id="survey-menu" class="hidden dropdown-list">
			<ul>
				<g:each in="${surveys}" var="survey">
					<li>
						<span>
							<a href="${createLink(controller: 'editSurvey', action:'summaryPage', params:[organisation: organisation?.id, survey: survey.id])}">
								<g:i18n field="${survey.names}"/>
							</a>
						</span>
					</li>
				</g:each>
			</ul>
		</div>
	</span>
</div>