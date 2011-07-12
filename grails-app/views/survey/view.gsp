<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="surveyPage.view.label"
		default="District Health System Portal" />
</title>
</head>
<body>
	<div id="survey">
		<div id="top-container">
			<div id="survey-iteration-box" class="box">
				<h5>Year:
					<span class="survey-highlight-title dropdown"> 
							<g:dateFormat format="yyyy" date="${surveyPage.survey.period.startDate}" />
					</span>
					</h5>
			</div>
			<div id="survey-objective-box" class="box">
				<div class="survey-container-left-side">
						<h5>
							Strategic Objective: <span class="survey-highlight-title">
							<g:if test="${surveyPage.subObjective != null}">
							<g:i18n field="${surveyPage.subObjective.objective.names}" />
							</g:if> <g:else> Select Strategic Objective </g:else>
							</span>
						</h5>
				</div>
				<div class="survey-container-right-side">
					<h5>
						Facility Name: <span class="survey-highlight-title">${surveyPage.organisationUnit.name}</span>
					</h5>
				</div>
				<div class="clear"></div>
			</div>
			<div class="clear"></div>
		</div>
		<div id="bottom-container">
			<div id="survey-left-objective-container" class="box">
				<div class="survey-strategic-object-title">
					<h5>Strategic Objectives</h5>
				</div>
				<g:if test="${surveyPage.survey.objectives != null}">
					<div id="survey-objective" class="objective">
						<ul id="survey-objective-list"
							class="objectives expandfirst collapsible">
							<g:each in="${surveyPage.survey.objectives}" var="objective">
								<li
									class="${surveyPage.subObjective?.objective?.id == objective.id?'current':''}">
									<a class="objective-link-${objective.id}" href="#"> <g:i18n
											field="${objective.names}" /> </a>
								<g:if test="${!objective.subObjectives.isEmpty()}" >
									<ul id="survey-subobjective-list-${objective.id}"
										class="survey-subobjective">
										<g:each in="${objective.subObjectives}" var="subObjective">
											<li><a id="subobjective-${subObjective.id}"
												class="flow-show-questions ${surveyPage.subObjective?.id == subObjective.id?'opened':''}"
												href="${createLink(controller:'survey', action:'view',params:[period:surveyPage.period.id,subObjective:subObjective.id,organisation: surveyPage.organisationUnit.id])}">
													<g:i18n field="${subObjective.names}" /> </a>
											</li>
										</g:each>
									</ul>
									</g:if>
									</li>
							</g:each>
						</ul>

					</div>
				</g:if>
				<g:else>
				Couldn't Load Objectives.
				</g:else>
			</div>
			<div id="survey-right-question-container" class="box">
				<g:if test="${surveyPage.subObjective!=null}">
					<div id="survey-subobjective-title-container">
						<h5>
							<g:i18n field="${surveyPage.subObjective.names}" />
						</h5>
						<div class="clear"></div>
					</div>
					<div id="survey-questions-container">
						<g:if test="!surveyPage.values.isEmpty()">
						
							<g:form url="[controller:'survey', action:'save', params: [organisation: surveyPage.organisationUnit.id, subObjective: surveyPage.subObjective.id, period: surveyPage.period.id]]" useToken="true">
							
								<g:set var="i" value="${1}" />
								<g:each in="${surveyPage.subObjective.questions}" var="question">
									<div class="question-in-block"><strong>${i++}) </strong>
										<g:render template="/survey/${question.getTemplate()}" model="[question: question, surveyElementValues: surveyPage.surveyElements]" /> 
									</div>
								</g:each>
								
								<div class="row">
									<button type="submit">Submit</button>
								</div>
							</g:form>
							
						</g:if>
						<g:else>No question available for this section</g:else>
						<div class="clear"></div>
					</div>

				</g:if>
				<g:else>
				Couldn't Load Sub Objectives.
				</g:else>
				<div class="clear"></div>
			</div>
			<!-- ADMIN SECTION -->
			<g:ifAdmin>
				<div class="hidden flow-container"></div>
			</g:ifAdmin>
			<!-- ADMIN SECTION END -->
			<script type="text/javascript">
				$(document).ready(function() {
					$('#survey-right-question-container').flow({
						onSuccess : function(data) {
							if (data.result == 'success') {
								location.reload();
							}
						}
					});

					//Menu Accordion functions
					initObjectiveMenus();
				});

				function initObjectiveMenus() {
					$('ul.objectives ul').hide();
					$.each($('ul.objectives'), function() {
						var current = $('.current').children();
						if (!current.hasClass('expandfirst')
								&& current.size() > 0) {
							current.addClass('expandfirst');
							current.show();
						}else{
							$('#' + this.id + '.expandfirst ul:first').show();
						}
					});
					$('ul.objectives li a')
							.click(
									function() {
										var checkElement = $(this).next();
										var parent = this.parentNode.parentNode.id;
										if ((checkElement.is('ul'))
												&& (checkElement.is(':visible'))) {
											if ($('#' + parent).hasClass(
													'collapsible')) {
												$('#' + parent + ' ul:visible')
														.slideUp('slow');
											}
											return false;
										}
										if ((checkElement.is('ul'))
												&& (!checkElement
														.is(':visible'))) {
											$('#' + parent + ' ul:visible')
													.slideUp('slow');
											checkElement.slideDown('slow');
											return false;
										}
									});
				}
			</script>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
	</div>
</body>
</html>