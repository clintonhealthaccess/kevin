<div id="survey-header" class="grey-rounded-box-top">
	<div>
		<g:if test="${surveyPage instanceof org.chai.kevin.survey.SurveyPage}">
			<div class="filter">
				<h5>Strategic Objective:</h5>
				<div class="dropdown">
					<a class="selected" href="#">
						<g:if test="${objective != null}">
							<g:i18n field="${objective.names}" />
						</g:if>
						<g:else>
							select an objective
						</g:else>
					</a>
					<div id="survey-menu" class="grey-rounded-box-bottom hidden dropdown-list">
						<g:render template="/survey/menu" model="[surveyPage: surveyPage]"/>
					</div>
				</div>
			</div>
			<div class="filter">
				<h5>Facility Name:</h5>
				<span>${organisation.name}</span>
			</div>
			<div class="filter">
				<h5>Year:</h5>
				<span> 
					<g:dateFormat format="yyyy" date="${period.startDate}" />
				</span>
			</div>
		</g:if>
		<g:else>
			<div class="filter">
				<h5>Survey:</h5>
				<div class="dropdown white-dropdown">
					<a class="selected" href="#">
						<g:if test="${survey != null}">
							<g:i18n field="${survey.names}" />
						</g:if>
						<g:else>
							Select a survey
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
				</div>
			</div>
			<div class="filter">
				<h5>Facility Name:</h5>
				<div class="dropdown white-dropdown">
					<g:if test="${organisation != null}">
						<a class="selected" href="#" data-type="organisation">${organisation.name}</a>
					</g:if>
					<g:else>
						<a class="selected" href="#" data-type="organisation">Select Organisation Unit</a>
					</g:else> 
					<div class="hidden dropdown-list">
						<ul>
							<g:render template="/templates/organisationTree"
								model="[controller: 'editSurvey', action: 'summaryPage', organisation: organisationTree, current: organisation, params:[survey: '1'], displayLinkUntil: displayLinkUntil]" />
						</ul>
					</div>
				</div>
			</div>
		</g:else>
	</div>
	<div class="clear"></div>
</div>
