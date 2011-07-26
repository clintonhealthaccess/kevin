<%@ page import="org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="surveyPage.objective.label" default="District Health System Portal" />
		</title>
	</head>
	<body>
		<div id="survey">
			<g:render template="/survey/header" model="[period: surveyPage.period, organisation: surveyPage.organisation, objective: surveyPage.objective]"/>
			
			<div id="bottom-container">
				<g:render template="/survey/menu" model="[surveyPage: surveyPage]"/>
				
				<g:message code="${flash.message}" default="${flash.default}"/>
				
				<div id="survey-right-question-container" class="box">
					<g:set value="${surveyPage.incompleteSections}" var="incompleteSections"/>
					<g:set value="${surveyPage.invalidQuestions}" var="invalidSectionMap"/>

					<g:set value="${surveyPage.getStatus(surveyPage.objective) == ObjectiveStatus.CLOSED}" var="closed"/>
					<g:set value="${surveyPage.getStatus(surveyPage.objective) == ObjectiveStatus.UNAVAILABLE}" var="unavailable"/>
					

					<g:if test="${closed}">
						This objective has been already been submitted. Please go on with the other sections.
					</g:if>
					<g:if test="${unavailable}">
						This objective can not yet be answered, please complete 
						<a href="${createLink(controller: 'survey', action: 'objectivePage', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id])}"><g:i18n field="${surveyPage.objective.dependency.names}"/></a>
						first.
					</g:if>
					
					<g:if test="${!closed&&!unavailable}">
						<div id="submit-objective" class="${!surveyPage.canSubmit()?'hidden':''}">
							This part has been completed successfully. If you are sure that you entered the right data, please click submit.
							<g:form url="[controller:'survey', action:'submit', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id]]" useToken="true">
								<button type="submit">Submit</button>
							</g:form>
						</div>

						<g:if test="${!incompleteSections.isEmpty()}">
							<div id="incomplete-sections">
								The following sections are incomplete, please go back and complete them:
								<ul>
									<g:each in="${incompleteSections}" var="section">
										<li>
											<a href="${createLink(controller:'survey', action:'sectionPage', params:[section:section.id, organisation: surveyPage.organisation.id])}">
												<g:i18n field="${section.names}"/>
											</a>
										</li>
									</g:each>
								</ul>
							</div>
						</g:if>
					
						<g:if test="${!invalidSectionMap.isEmpty()}">
							<div id="invalid-questions-container">
								The following questions do not pass validation, please check:
								<form id="survey-form">
									<div id="invalid-questions">
										<g:render template="/survey/invalidSections" model="[invalidSectionMap: invalidSectionMap, surveyPage: surveyPage]"/>
									</div>
								</form>
							</div>
						</g:if>
					</g:if>
					<div class="clear"></div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			function surveyValueChanged(id, element) {
				var data = $('#survey-form').serialize();
				data += '&element='+id;
				
				$.ajax({
					type : 'POST',
					dataType: 'json',
					data : data,
					url : "${createLink(controller:'survey', action:'saveValue', params: [organisation: surveyPage.organisation.id, section: surveyPage.section?.id, objective: surveyPage.objective?.id])}",
					success : function(data, textStatus) {
						if (data.result == "success") {
							if (data.status == "valid") {
								$('#invalid-questions').html(data.invalidSectionsHtml)
								
								if ($('#invalid-questions .invalid-question').length == 0) {
									// we get rid of the invalid question section
									
									$('#invalid-questions-container').remove();
									if ($('#incomplete-sections').length == 0) $('#submit-objective').show();
								}
							}					
							if (data.status == "invalid") {
								$(element).parents('.question').addClass('errors');
								$(element).parents('.question').html(data.html);
							}
							
							
							$('#objective-'+data.objective.id+' .objective-status').addClass('hidden');
							$('#objective-'+data.objective.id+' .objective-status-'+data.objective.status.toLowerCase()).removeClass('hidden');
							$(data.sections).each(function(key, section) {
								$('#section-'+section.id+' .section-status').addClass('hidden');
								$('#section-'+section.id+' .section-status-'+section.status.toLowerCase()).removeClass('hidden');
							});
						}
					}
				});
			}
			
			function getInvalidQuestions(sections) {
				var questions = [];
				$(sections).each(function(index, section) {
					$(section.invalidQuestions).each(function(index, question) {
						question.section = section;
						questions.push(question);
					});
				});
				return questions;
			}
		</script>
	</body>
</html>