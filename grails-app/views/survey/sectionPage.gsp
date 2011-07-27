<%@ page import="org.chai.kevin.survey.SurveyPage.SectionStatus" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="surveyPage.section.label" default="District Health System Portal" />
		</title>
	</head>
	<body>
		<g:set var="closed" value="${surveyPage.getStatus(surveyPage.section) == SectionStatus.CLOSED}"/>
		<g:set var="unavailable" value="${surveyPage.getStatus(surveyPage.section) == SectionStatus.UNAVAILABLE}"/>
		<g:set var="readonly" value="${closed||unavailable}"/>
	
		<div id="survey">
			<g:render template="/survey/header" model="[period: surveyPage.period, organisation: surveyPage.organisation, objective: surveyPage.objective]"/>
			
			<div id="bottom-container">
				<g:render template="/survey/menu" model="[surveyPage: surveyPage]"/>
				
				<g:message code="${flash.message}" default="${flash.default}"/>
				
				<div id="survey-right-question-container" class="box">
				
					<g:if test="${closed}">
						This section has been submitted, you can view your answer here but you cannot change them.
					</g:if> 
					<g:if test="${unavailable}">
						This section can not yet be answered, please complete 
						<a href="${createLink(controller: 'survey', action: 'objectivePage', params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id])}"><g:i18n field="${surveyPage.objective.dependency.names}"/></a>
						first.
					</g:if> 
					
					<div class="rounded-box-top">
						<h5>
							<g:i18n field="${surveyPage.section.names}" />
						</h5>
					</div>
					<div class="rounded-box-bottom">
					
						<g:form id="survey-form" url="[controller:'survey', action:'save', params: [organisation: surveyPage.organisation.id, section: surveyPage.section.id, survey: surveyPage.survey.id]]" useToken="true">
							<g:set var="i" value="${1}" />
							<g:each in="${surveyPage.section.getQuestions(surveyPage.organisation.organisationUnitGroup)}" var="question">
								<div class="question ${surveyPage.isValid(question)?'':'errors'}">
									<g:render template="/survey/question/${question.getType()}" model="[question: question, surveyElementValues: surveyPage.surveyElements, organisationUnitGroup: surveyPage.organisation.organisationUnitGroup, readonly: readonly]" />
								</div> 
							</g:each>
							
							<g:if test="${!closed}">
								<button type="submit">Save</button>
							</g:if>
						</g:form>
							
					</div>
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
								// TODO remove class error
								$(element).parents('.question').find('.error-list').remove()
								$(element).parents('.question').removeClass('errors');
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
		</script>
	</body>
</html>