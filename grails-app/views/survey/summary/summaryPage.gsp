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
			<div class="subnav">
				<g:render template="/survey/summary/surveyFilter"/>
				<g:locationFilter linkParams="${[survey: currentSurvey?.id, program: currentProgram?.id, section: currentSection?.id, sort: SurveySummaryPage.PROGRESS_SORT, order:'desc']}" selected="${currentLocation}"/>
			</div>
						
			<div class="main">			
				<g:if test="${summaryPage == null}">
					<p class="nav-help"><g:message code="survey.summary.selectsurveylocation.text"/></p>
				</g:if>
				<g:else>
					<div class="push-20">
						<div class="push-10">
							<g:message code="location.label"/>: <g:i18n field="${currentLocation.names}"/>
						</div>
						<div>
							<g:message code="survey.summary.progress"/>: <span class="js_progress-bar">${summaryPage.summary.completedQuestions}/${summaryPage.summary.questions}</span>
						</div>
						<g:if test="${currentLocation != null && currentSurvey != null && currentProgram == null && currentSection == null}">
							<div>
								<g:form url="${createLink(action: 'submitAll', params: [location: currentLocation.id, survey: currentSurvey.id])}">
									<button type="submit">
										Submit All <g:i18n field="${currentSurvey.names}" />
										<br />
										Surveys for <g:i18n field="${currentLocation.names}" />
									</button>
								</g:form>
							</div>
						</g:if>			
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