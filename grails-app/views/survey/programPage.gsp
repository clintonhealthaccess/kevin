<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.programPage.label" /></title>
		
		<r:require modules="survey"/>
	</head>
	<body>
		<div">
			<g:render template="/survey/header" model="[period: surveyPage.period, location: surveyPage.location, program: surveyPage.program]"/>
			
			<div class="main" id="js_survey">
				<g:set value="${surveyPage.enteredPrograms[surveyPage.program].closed}" var="closed"/>
				<g:set var="readonly" value="${surveyPage.isReadonly(surveyPage.program)}"/>
				
				<g:if test="${closed}">
					<div class="success-box">
						<p class="success">
							<g:message code="survey.program.submitted.text" />
							<shiro:hasPermission permission="admin:survey">
								<a href="${createLink(controller: 'editSurvey', action: 'reopen', params: [location: surveyPage.location.id, program: surveyPage.program.id])}">
									<g:message code="survey.program.reopen.text"/>
								</a>
							</shiro:hasPermission>
						</p>
					</div>
				</g:if>
				
				<g:if test="${!closed}">
					<div id="js_submit-program" class="${!surveyPage.canSubmit(surveyPage.program)?'hidden':''} success-box">
						<p class="success"><g:message code="survey.program.ready.text" /></p>
						<g:form url="[controller:'editSurvey', action:'submit', params: [location: surveyPage.location.id, program: surveyPage.program.id]]">
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
					callback: valueChangedInProgram,
					url: "${createLink(controller:'editSurvey', action:'saveValue', params: [location: surveyPage.location.id, section: surveyPage.section?.id, program: surveyPage.program?.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});
			});
		
			function valueChangedInProgram(dataEntry, data, element) {
				if (data.status == 'success') {
					// we go through all the sections
					$.each(data.sections, function(index, section) {
						$('#section-'+section.id).find('.section-status').addClass('hidden');
						$('#section-'+section.id).find('.section-status-'+section.status).removeClass('hidden');
					});
					
					// we go through the programs
					$.each(data.programs, function(index, program) {
						$('#program-'+program.id).find('.program-status').addClass('hidden');
						$('#program-'+program.id).find('.program-status-'+program.status).removeClass('hidden');
					});
				
					$('#incomplete-sections-container').html(data.incompleteSections);
					$('#invalid-questions-container').html(data.invalidQuestions);
					
					if ($.trim(data.invalidQuestions) == '' && $.trim(data.incompleteSections) == '') $('#js_submit-program').removeClass('hidden');
					else $('#js_submit-program').addClass('hidden');
					
					dataEntry.enableAfterLoading();
				}
				else {
					alert(self.settings.messages['dataentry.saving.program.closed.text']);
				}
			}
		</r:script>
	</body>
</html>