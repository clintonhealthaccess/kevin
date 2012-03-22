<r:require module="dropdown"/>

<div class="subnav">
	<div>
		<div class="filter">
			<span class="js_dropdown dropdown">
				<a class="program selected" href="#">
					<g:if test="${program != null}">
						<g:i18n field="${program.names}" />
					</g:if>
					<g:else>
					<g:message code="survey.labels.program.select" />	
					</g:else>
				</a>
				<div class="hidden dropdown-list js_dropdown-list">
					<g:render template="/survey/menu" model="[surveyPage: surveyPage]"/>
				</div>
			</span>
		</div>
		<div class="filter">
			<span class="bold"><g:message code="datalocation.label" />:</span>
			<span><g:i18n field="${location.names}"/></span>
		</div>
		<div class="filter">
			<span class="bold"><g:message code="survey.labels.period" />:</span>
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
