<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.summaryPage.label" default="District Health System Portal" /></title>
		
		<r:require modules="progressbar,dropdown,explanation,survey"/>
	</head>
	<body>
		<div id="survey">
		
			<g:if test="${summaryPage != null}">			
			<g:set var="section" value="${summaryPage.section}" />
			<g:set var="objective" value="${summaryPage.objective}" />
			<g:set var="survey" value="${summaryPage.survey}" />						
			</g:if>

			<div id="survey-header" class="subnav">
			<g:render template="/survey/surveyFilter" />
			<g:render template="/templates/organisationFilter" model="[currentOrganisation: organisation, linkParams:[survey: survey?.id, objective: objective?.id, section: section?.id]]"/>
				<div class="clear"></div>
			</div>
						
			<div class="main">			
				
				<g:if test="${organisation == null || (survey == null && objective == null && section == null)}">
				<p class="help"><g:message code="survey.summary.selectsurveyfacility.text" default="Please select a survey and a facility to get to the respective survey."/></p>
				</g:if>
				<g:else>				
					
					<g:if test="${section != null}">
						<g:render template="/survey/summarySectionTable" model="[linkParams:[section: section.id]]"/>
					</g:if>
					<g:elseif test="${objective != null}">
						<g:render template="/survey/summaryObjectiveTable" model="[linkParams:[objective: objective.id]]"/>
					</g:elseif>
					<g:elseif test="${survey != null}">
						<g:render template="/survey/summarySurveyTable" model="[linkParams:[survey: survey.id]]"/>
					</g:elseif>
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