<%@ page import="org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>

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
			
			<div class="grey-rounded-box-bottom">
				<g:set value="${surveyPage.getIncompleteSections(surveyPage.objective)}" var="incompleteSections"/>
				<g:set value="${surveyPage.getInvalidQuestions(surveyPage.objective)}" var="invalidQuestions"/>

				<g:set value="${surveyPage.objectives[surveyPage.objective].status == ObjectiveStatus.CLOSED}" var="closed"/>
				<g:set value="${surveyPage.objectives[surveyPage.objective].status == ObjectiveStatus.UNAVAILABLE}" var="unavailable"/>
				<g:set var="canSave" value="${SecurityUtils.subject.isPermitted('survey:save')}"/>
				
				<g:set var="readonly" value="${closed||unavailable||!canSave}"/>
				
				<g:if test="${flash.message}">
					<div class="rounded-box-top rounded-box-bottom flash-info">
						<g:message code="${flash.message}" default="${flash.default}"/>
					</div>
				</g:if>
				
				<g:if test="${closed}">
					<div class="rounded-box-top rounded-box-bottom">
						This objective has been already been submitted. Please go on with the other sections.
						<shiro:hasPermission permission="admin:survey">
							<a href="${createLink(controller: 'editSurvey', action: 'reopen', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id])}">Reopen this objective.</a>
						</shiro:hasPermission>
					</div>
				</g:if>
				<g:if test="${unavailable}">
					<div class="rounded-box-top rounded-box-bottom">
						This objective can not yet be answered, please complete 
						<a href="${createLink(controller: 'editSurvey', action: 'objectivePage', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.dependency.id])}">
							<g:i18n field="${surveyPage.objective.dependency.names}"/>
						</a>
						first.
					</div>
				</g:if>
				
				<g:if test="${!closed && !unavailable}">
					<div class="rounded-box-top rounded-box-bottom">
						<div id="submit-objective" class="${!surveyPage.canSubmit(surveyPage.objective)?'hidden':''}">
							This part has been completed successfully. If you are sure that you entered the right data, please click submit.
							<g:form url="[controller:'editSurvey', action:'submit', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id]]">
								<button type="submit">Submit</button>
							</g:form>
						</div>

						<g:if test="${!incompleteSections.isEmpty()}">
							<div id="incomplete-sections">
								The following sections are incomplete, please go back and complete them:
								<ul>
									<g:each in="${incompleteSections}" var="section">
										<li>
											<a href="${createLink(controller:'editSurvey', action:'sectionPage', params:[section:section.id, organisation: surveyPage.organisation.id])}">
												<g:i18n field="${section.names}"/>
											</a>
										</li>
									</g:each>
								</ul>
							</div>
						</g:if>
					</div>
				
					<g:if test="${!invalidQuestions.isEmpty()}">
						<div id="invalid-questions-container">
							<div class="rounded-box-top">The following questions do not pass validation, please check:</div>
							<form id="survey-form">
								<div id="invalid-questions">
									<g:render template="/survey/invalidQuestions" model="[invalidQuestions: invalidQuestions, surveyPage: surveyPage, readonly: readonly]"/>
								</div>
							</form>
						</div>
					</g:if>
				</g:if>
			</div>
		</div>
		<g:if test="${!readonly}">
			<script type="text/javascript">
				$(document).ready(function() {
					$('#survey-form').delegate('input, select, textarea', 'change', function(){
						var element = $(this).parents('.element');
						surveyValueChanged(element, valueChangedInSection);
					});
					$('#survey-form').delegate('a.outlier-validation', 'click', function(){
						var element = $(this).parents('.element');
						surveyValueChanged(element, valueChangedInSection);
						return false;
					});
					
					$('#survey-form').delegate('.element-list-add', 'click', function(){
						listAddClick(this, valueChangedInSection);
						return false;
					});
					$('#survey-form').delegate('.element-list-remove', 'click', function(){
						listRemoveClick(this, valueChangedInSection);
						return false;
					});
				});
			
				function valueChangedInObjective(data, element) {
					if (data.status == "invalid") {
						$(element).parents('.question').addClass('errors');
						$(element).parents('.question-container').html(data.html);
					}
					if (data.status == "valid") {
						$('#invalid-questions').html(data.invalidSectionsHtml)
						
						if ($('#invalid-questions .invalid-question').length == 0) {
							// we get rid of the invalid question section
							
							$('#invalid-questions-container').remove();
							if ($('#incomplete-sections').length == 0) $('#submit-objective').show();
						}
					}					
				}
			</script>
		</g:if>
	</body>
</html>