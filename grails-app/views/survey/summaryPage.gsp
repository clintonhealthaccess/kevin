<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.summaryPage.label" default="District Health System Portal" /></title>
		
		<r:require modules="progressbar,dropdown,explanation,survey"/>
	</head>
	<body>
		<g:set var="survey" value="${summaryPage.survey}"/>
		<g:set var="organisation" value="${summaryPage.organisation}"/>
		<div id="survey">
			<div id="survey-header" class="subnav">
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
				<div class="filter">
					<span class="bold"><g:message code="facility.label" default="Facility Name"/>:</span>
					<span class="dropdown subnav-dropdown">
						<g:if test="${organisation != null}">
							<a class="selected" href="#" data-type="organisation">${organisation.name}</a>
						</g:if>
						<g:else>
							<a class="selected" href="#" data-type="organisation"><g:message code="default.select.label" args="[message(code:'location.label')]" default="Select location"/></a>
						</g:else> 
						<div class="hidden dropdown-list">
							<ul>
								<g:render template="/templates/organisationTree"
									model="[controller: 'editSurvey', action: 'summaryPage', organisation: organisationTree, current: organisation, params:[survey: '1'], displayLinkUntil: displayLinkUntil]" />
							</ul>
						</div>
					</span>
				</div>
				<div class="clear"></div>
			</div>
			
			
			<div class="main">
				<g:if test="${summaryPage.organisation == null || summaryPage.survey == null}">
				<p class="help"><g:message code="survey.summary.selectsurveyfacility.text" default="Please select a survey and a facility to get to the respective survey."/></p>
				</g:if>
				<g:else>
					<div id="survey-summary">
						<table class="listing">
							<thead>
								<th><g:message code="facility.label" default="Facility"/></th>
								<th><g:message code="survey.summary.objectivesubmitted.label" default="Objectives submitted"/></th>
								<th><g:message code="survey.summary.progress" default="Overall progress"/></th>
								<th colspan="3"></th>
							</thead>
							<tbody>
								<g:each in="${summaryPage.facilities}" var="facility">
									<g:set var="organisationSummary" value="${summaryPage.getOrganisationSummary(facility)}"/>
									<tr>
										<td class="objective-table-link" data-facility="${facility.id}">
											<a href="${createLink(controller: 'editSurvey', action: 'objectiveTable', params: [survey: summaryPage.survey.id, organisation: facility.id])}">${facility.name}</a>
										</td>
										<td>${organisationSummary.submittedObjectives}/${organisationSummary.objectives}</td>
										<td><span class="progress-bar">${organisationSummary.completedQuestions}/${organisationSummary.questions}</span></td>
										<td><a href="${createLink(controller: 'editSurvey', action: 'surveyPage', params: [survey: summaryPage.survey.id, organisation: facility.id])}"><g:message code="survey.summary,viewsurvey.label" default="View Survey"/></a></td>
										<td><a href="${createLink(controller: 'editSurvey', action: 'refresh', params: [survey: summaryPage.survey.id, organisation: facility.id])}"><g:message code="survey.summary.refreshsurvey.label" default="Refresh Survey"/></a></td>
										<td><a href="${createLink(controller: 'editSurvey', action: 'print', params: [survey: summaryPage.survey.id, organisation: facility.id])}" target="_blank"><g:message code="survey.summary.printsurvey.label" default="Print Survey"/></a></td>
									</tr>
									<tr class="explanation-row">
										<td colspan="6">
											<div class="explanation-cell" id="explanation-${facility.id}"></div>
										</td>
									</tr>
								</g:each>
							</tbody>
						</table>
					</div>
				</g:else>
			</div>
		</div>
		
		<r:script>
			function progressBar() {
				$(".progress-bar").each(function(){
					var values = $(this).html().split('/');
					
					if (values.length == 2) {
						var value = (values[0]/values[1])*100;
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