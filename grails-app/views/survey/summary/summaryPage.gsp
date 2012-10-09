<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.summary.title" /></title>
		
		<r:require modules="progressbar,dropdown,explanation,survey"/>
	</head>
	<body>
		<div>
			<div class="filter-bar">
				<g:render template="/survey/summary/surveyFilter"/>
				<g:locationFilter linkParams="${[survey: currentSurvey?.id, program: currentProgram?.id, section: currentSection?.id, sort: SurveySummaryPage.PROGRESS_SORT, order:'desc']}" 
					selected="${currentLocation}" selectedTypes="${currentLocationTypes}" skipLevels="${locationSkipLevels}"/>
				<g:dataLocationTypeFilter linkParams="${params}" selected="${currentLocationTypes}"/>
			</div>
						
			<div class="main">			
				<g:if test="${summaryPage == null}">
					<p class="nav-help"><g:message code="survey.summary.selectsurveylocation.text"/></p>
				</g:if>
				<g:else>
					<div class="push-10">
						<div class="push-10">
							<g:message code="location.label"/>: <g:i18n field="${currentLocation.names}"/>
						</div>
						<div>
							<g:message code="survey.summary.progress"/>: <span class="js_progress-bar">${summaryPage.summary.completedQuestions}/${summaryPage.summary.questions}</span>
						</div>									
					</div>
					<div class="js_dropdown dropdown push-20"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<shiro:hasPermission permission="surveySummary:refresh">
									<li>
										<a href="${createLinkWithTargetURI(controller: 'task', action: 'create', 
											params: params << [class: 'RefreshSurveyTask', surveyId: currentSurvey.id, locationId: currentLocation.id])}">
											<g:message code="survey.summary.refreshsurvey.label" />
										</a>
									</li>
								</shiro:hasPermission>
								<shiro:hasPermission permission="surveySummary:submitAll">
									<g:if test="${!submitSkipLevels.contains(currentLocation.level)}">
										<li>
											<a href="${createLink(controller: 'surveySummary', action: 'submitAll', 
												params: params << [survey: currentSurvey?.id, program: currentProgram?.id, submitLocation: currentLocation.id])}">
												<g:message code="survey.summary.submitallprogram.label" />
											</a>
										</li>
									</g:if>
								</shiro:hasPermission>
							</ul>
						</div>
					</div>
					
					<g:render template="${template}"/>				
				</g:else>
				
			</div>
		</div>
		
		<r:script>
			${render(template:'/templates/progressImages')}
		
			$(document).ready(function() {
				$('.program-table-link').bind('click', function() {
    				var location = $(this).data('location');
    				explanationClick(this, location, function(){progressBar();});
    				return false;
    			});
			});
		</r:script>
	</body>
</html>