<%@ page import="org.chai.kevin.survey.SummaryPage" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.summaryPage.label" default="District Health System Portal" /></title>
		
		<r:require modules="progressbar,dropdown,explanation,survey"/>
	</head>
	<body>
		<div id="survey">
		
			<div id="survey-header" class="subnav">
				<g:render template="/survey/summary/surveyFilter"/>
				<g:render template="/templates/organisationFilter" model="[currentOrganisation: organisation, linkParams:[survey: currentSurvey?.id, objective: currentObjective?.id, section: currentSection?.id, sort: SummaryPage.PROGRESS_SORT, order:'desc']]"/>
				<div class="clear"></div>
			</div>
						
			<div class="main">			
				<g:if test="${summaryPage == null}">
					<p class="help"><g:message code="survey.summary.selectsurveyfacility.text" default="Please select a survey and a facility to get to the respective survey."/></p>
				</g:if>
				<g:else>
					<g:render template="${template}"/>
				</g:else>
			</div>
		</div>
		
		<r:script>
			function progressBar() {
				$(".progress-bar").each(function(){
					var values = $(this).html().split('/');
					
					if (values.length == 2) {
						var value;
						if(values[1] == 0) value = 0;
						else value = (values[0]/values[1])*100;
						$(this).progressBar(value, {
							steps: 0,
							boxImage: "${resource(dir:'js/jquery/progressbar/images',file:'progressbar.gif')}",
							barImage: {
								0:  "${resource(dir:'js/jquery/progressbar/images',file:'progressbg_red.gif')}",
								30: "${resource(dir:'js/jquery/progressbar/images',file:'progressbg_yellow.gif')}",
								70: "${resource(dir:'js/jquery/progressbar/images',file:'progressbg_green.gif')}"
							}
						});
					}
				});
			} 
		
			$(document).ready(function() {
				progressBar();				

				$('.objective-table-link').bind('click', function() {
    				var facility = $(this).data('facility');
    				explanationClick(this, facility, function(){progressBar();});
    				return false;
    			});
			});
		</r:script>
	</body>
</html>