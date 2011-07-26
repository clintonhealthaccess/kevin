<%@ page import="org.chai.kevin.survey.SurveyPage.SectionStatus" %>
<%@ page import="org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus" %>

<div id="survey-left-objective-container" class="box">
	<div class="survey-strategic-object-title">
		<h5>Strategic Objectives</h5>
	</div>
	
	<ul id="survey-objective-list" class="objectives expandfirst collapsible">
		<g:each in="${surveyPage.survey.getObjectives(surveyPage.organisation.organisationUnitGroup)}" var="objective">
			<g:set var="objectiveStatus" value="${surveyPage.getStatus(objective)}"/>
			
			<li id="objective-${objective.id}" class="${surveyPage.section?.objective?.id == objective.id?'current':''}">
				<a class="item" href="${createLink(controller:'survey', action:'objectivePage', params:[objective:objective.id, organisation: surveyPage.organisation.id])}">
					<g:i18n field="${objective.names}" />
					<div class="float-right">
						<span class="objective-status-complete objective-status ${objectiveStatus != ObjectiveStatus.COMPLETE?'hidden':''}">OK</span>
						<span class="objective-status-invalid  objective-status ${objectiveStatus != ObjectiveStatus.INVALID?'hidden':''}">ERR</span>
						<span class="objective-status-incomplete objective-status ${objectiveStatus != ObjectiveStatus.INCOMPLETE?'hidden':''}"></span>
						<span class="objective-status-closed objective-status ${objectiveStatus != ObjectiveStatus.CLOSED?'hidden':''}">X</span>
						<span class="objective-status-unavailable objective-status ${objectiveStatus != ObjectiveStatus.UNAVAILABLE?'hidden':''}">N/A</span>
					</div>
				</a>
				<g:if test="${surveyPage.objective.equals(objective)}">
					<ul class="survey-section">
						<g:each in="${objective.getSections(surveyPage.organisation.organisationUnitGroup)}" var="section">
							<g:set var="sectionStatus" value="${surveyPage.getStatus(section)}"/>

							<li id="section-${section.id}">
								<a class="item ${surveyPage.section?.id == section.id?'opened':''}" href="${createLink(controller:'survey', action:'sectionPage', params:[section:section.id, organisation: surveyPage.organisation.id])}">
									<g:i18n field="${section.names}" />
									<div class="float-right">
										<span class="section-status-complete section-status ${sectionStatus != SectionStatus.COMPLETE?'hidden':''}">OK</span>
										<span class="section-status-invalid section-status ${sectionStatus != SectionStatus.INVALID?'hidden':''}">ERR</span>
										<span class="section-status-incomplete section-status ${sectionStatus != SectionStatus.INCOMPLETE?'hidden':''}"></span>
										<span class="section-status-closed section-status ${sectionStatus != SectionStatus.CLOSED?'hidden':''}">X</span>
										<span class="section-status-unavailable section-status ${sectionStatus != SectionStatus.UNAVAILABLE?'hidden':''}">N/A</span>
									</div>
								</a>
							</li>
						</g:each>
					</ul>
				</g:if>
			</li>
		</g:each>
	</ul>
	
	<script type="text/javascript">
		$(document).ready(function(){
			//Menu Accordion functions
// 			initObjectiveMenus();
		});
	
// 		function initObjectiveMenus() {
// 			$('ul.objectives ul').hide();
// 			$.each($('ul.objectives'), function() {
// 				var current = $('.current').children();
// 				if (!current.hasClass('expandfirst')
// 						&& current.size() > 0) {
// 					current.addClass('expandfirst');
// 					current.show();
// 				}else{
// 					$('#' + this.id + '.expandfirst ul:first').show();
// 				}
// 			});
	// 					$('ul.objectives li a').click(
	// 						function() {
	// 							var checkElement = $(this).next();
	// 							var parent = this.parentNode.parentNode.id;
	// 							if ((checkElement.is('ul'))
	// 									&& (checkElement.is(':visible'))) {
	// 								if ($('#' + parent).hasClass(
	// 										'collapsible')) {
	// 									$('#' + parent + ' ul:visible')
	// 											.slideUp('slow');
	// 								}
	// 								return false;
	// 							}
	// 							if ((checkElement.is('ul'))
	// 									&& (!checkElement
	// 											.is(':visible'))) {
	// 								$('#' + parent + ' ul:visible')
	// 										.slideUp('slow');
	// 								checkElement.slideDown('slow');
	// 								return false;
	// 							}
	// 						}
	// 					);
// 		}
	</script>
	
</div>