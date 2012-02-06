<r:require module="dropdown"/>

<div id="survey-header" class="subnav">
	<div>
		<div class="filter">
			<span class="bold"><g:message code="survey.labels.objective" default="Strategic Objective" />:</span>
			<span class="js_dropdown dropdown">
				<a class="selected" href="#">
					<g:if test="${objective != null}">
						<g:i18n field="${objective.names}" />
					</g:if>
					<g:else>
					<g:message code="survey.labels.objective.select" default="Select an objective" />	
					</g:else>
				</a>
				<div id="survey-menu" class="hidden dropdown-list js_dropdown-list">
					<g:render template="/survey/menu" model="[surveyPage: surveyPage]"/>
				</div>
			</span>
		</div>
		<div class="filter">
			<span class="bold"><g:message code="facility.label" default="Facility Name" />:</span>
			<span><g:i18n field="${location.names}"/></span>
		</div>
		<div class="filter">
			<span class="bold"><g:message code="survey.labels.period" default="Year" />:</span>
			<span> 
				<g:dateFormat format="yyyy" date="${period.startDate}" />
			</span>
		</div>
	</div>
	<shiro:hasPermission permission="admin">
		<div class="right"><a href="#" onclick="$('.admin-hint').toggle();return false;">Toggle element information</a></div>
	</shiro:hasPermission>
	<div class="clear"></div>
</div>
