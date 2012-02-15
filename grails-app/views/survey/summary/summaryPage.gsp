<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.summaryPage.label" default="District Health System Portal" /></title>
		
		<r:require modules="progressbar,dropdown,explanation,survey"/>
	</head>
	<body>
		<div>
			<div class="subnav">
				<g:render template="/survey/summary/surveyFilter"/>
				<g:locationFilter linkParams="${[survey: currentSurvey?.id, objective: currentObjective?.id, section: currentSection?.id, sort: SurveySummaryPage.PROGRESS_SORT, order:'desc']}" selected="${currentLocation}"/>
			</div>
						
			<div class="main">			
				<g:if test="${summaryPage == null}">
					<p class="help"><g:message code="survey.summary.selectsurveyfacility.text" default="Please select a survey and a facility to get to the respective survey."/></p>
				</g:if>
				<g:else>
					<div class="push-20">
						<div class="push-10">
							<g:message code="location.label"/>: <g:i18n field="${currentLocation.names}"/>
						</div>
						<div>
							<g:message code="survey.summary.progress"/>: <span class="js_progress-bar">${summaryPage.summary.completedQuestions}/${summaryPage.summary.questions}</span>
						</div>
					</div>
					<g:render template="${template}"/>
				</g:else>
			</div>
		</div>
		
		<r:script>
			${render(template:'/templates/progressImages')}
		
			$(document).ready(function() {
				$('.objective-table-link').bind('click', function() {
    				var facility = $(this).data('facility');
    				explanationClick(this, facility, function(){progressBar();});
    				return false;
    			});
			});
		</r:script>
	</body>
</html>