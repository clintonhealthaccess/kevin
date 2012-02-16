<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.objectivePage.label" default="District Health System Portal" /></title>
		
		<r:require modules="survey"/>
	</head>
	<body>
		<div">
			<g:render template="/survey/header" model="[period: surveyPage.period, location: surveyPage.location, objective: surveyPage.objective]"/>
			
			<div class="main" id="js_survey">
				<g:set value="${surveyPage.enteredObjectives[surveyPage.objective].closed}" var="closed"/>
				<g:set var="readonly" value="${surveyPage.isReadonly(surveyPage.objective)}"/>
				
				<g:if test="${closed}">
					<div class="success-box">
						<p class="success">
							<g:message code="survey.objective.submitted.text" default="This objective has already been submitted, please go on with the other sections." />
							<shiro:hasPermission permission="admin:survey">
								<a href="${createLink(controller: 'editSurvey', action: 'reopen', params: [location: surveyPage.location.id, objective: surveyPage.objective.id])}">
									<g:message code="survey.objective.reopen.text"/>
								</a>
							</shiro:hasPermission>
						</p>
					</div>
				</g:if>
				
				<g:if test="${!closed}">
					<div id="js_submit-objective" class="${!surveyPage.canSubmit(surveyPage.objective)?'hidden':''} success-box">
						<p class="success"><g:message code="survey.objective.ready.text" default="This part has been completed successfully. If you are sure that you entered the right data, please click submit." /></p>
						<g:form url="[controller:'editSurvey', action:'submit', params: [location: surveyPage.location.id, objective: surveyPage.objective.id]]">
							<button type="submit">Submit</button>
						</g:form>
					</div>

					<div id="incomplete-sections-container">
						<g:render template="/survey/incompleteSections" model="[surveyPage: surveyPage]" />
					</div>
				
					<div id="invalid-questions-container">
						<g:render template="/survey/invalidQuestions" model="[surveyPage: surveyPage]" />
					</div>
				</g:if>
			</div>
		</div>
		<r:script>
			$(document).ready(function() {
				${render(template:'/templates/messages')}
			
				new DataEntry({
					element: $('#js_survey'),
					callback: valueChangedInObjective,
					url: "${createLink(controller:'editSurvey', action:'saveValue', params: [location: surveyPage.location.id, section: surveyPage.section?.id, objective: surveyPage.objective?.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});
			});
		
			function valueChangedInObjective(dataEntry, data, element) {
				if (data.status == 'success') {
					// we go through all the sections
					$.each(data.sections, function(index, section) {
						$('#section-'+section.id).find('.section-status').addClass('hidden');
						$('#section-'+section.id).find('.section-status-'+section.status).removeClass('hidden');
					});
					
					// we go through the objectives
					$.each(data.objectives, function(index, objective) {
						$('#objective-'+objective.id).find('.objective-status').addClass('hidden');
						$('#objective-'+objective.id).find('.objective-status-'+objective.status).removeClass('hidden');
					});
				
					$('#incomplete-sections-container').html(data.incompleteSections);
					$('#invalid-questions-container').html(data.invalidQuestions);
					
					if ($.trim(data.invalidQuestions) == '' && $.trim(data.incompleteSections) == '') $('#js_submit-objective').removeClass('hidden');
					else $('#js_submit-objective').addClass('hidden');
					
					dataEntry.enableAfterLoading();
				}
				else {
					alert(self.settings.messages['dataentry.saving.objective.closed.text']);
				}
			}
		</r:script>
	</body>
</html>