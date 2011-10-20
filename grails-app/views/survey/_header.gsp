<r:require module="dropdown"/>

<div id="survey-header" class="grey-rounded-box-top">
	<div>
		<div class="filter">
			<span class="bold"><g:message code="survey.strategicobjective.label" default="Strategic Objective" />:</span>
			<span class="dropdown">
				<a class="selected" href="#">
					<g:if test="${objective != null}">
						<g:i18n field="${objective.names}" />
					</g:if>
					<g:else>
					<g:message code="survey.selectanobjective.label" default="Select an objective" />	
					</g:else>
				</a>
				<div id="survey-menu" class="grey-rounded-box-bottom hidden dropdown-list">
					<g:render template="/survey/menu" model="[surveyPage: surveyPage]"/>
				</div>
			</span>
		</div>
		<div class="filter">
			<span class="bold"><g:message code="survey.facilityname.label" default="Facility Name" />:</span>
			<span>${organisation.name}</span>
		</div>
		<div class="filter">
			<span class="bold"><g:message code="general.text.year" default="Year" />:</span>
			<span> 
				<g:dateFormat format="yyyy" date="${period.startDate}" />
			</span>
		</div>
	</div>
	<div class="clear"></div>
</div>
