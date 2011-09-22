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
			<div id="survey-header" class="grey-rounded-box-top">
				<div class="filter">
					<span class="bold">Survey:</span>
					<span class="dropdown white-dropdown">
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
					</span>
				</div>
				<div class="filter">
					<span class="bold">Facility Name:</span>
					<span class="dropdown white-dropdown">
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
					</span>
				</div>
				<div class="clear"></div>
			</div>
			
			
			<div class="grey-rounded-box-bottom">
				<g:if test="${summaryPage.organisation == null || summaryPage.survey == null}">
					Please select a survey and a facility to get to the respective survey.
				</g:if>
				<g:else>
					<div id="survey-summary">
						<table>
							<thead>
								<th>Facility</th>
								<th>Objectives submitted</th>
								<th>Overall progress</th>
								<th></th>
								<th></th>
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
										<td><a href="${createLink(controller: 'editSurvey', action: 'surveyPage', params: [survey: summaryPage.survey.id, organisation: facility.id])}">view survey</a></td>
										<td><a href="${createLink(controller: 'editSurvey', action: 'refresh', params: [survey: summaryPage.survey.id, organisation: facility.id])}">refresh survey</a></td>
										<td><a href="${createLink(controller: 'survey', action: 'print', params: [survey: summaryPage.survey.id, organisation: facility.id])}" target="_blank">Print Survey</a></td>
									</tr>
									<tr>
										<td colspan="5">
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