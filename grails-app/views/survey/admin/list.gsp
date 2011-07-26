<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="surveyAdminPage.view.label"
		default="District Health System Portal" /></title>
</head>
<body>
	<div id="survey-admin">
			<div id="top-container" class="box">
				<ul class="survey-admin-menu-list">
				<li><a href="${createLink(controller:'admin', action:'survey')}">Surveys</a>
					</li>
					<g:if test="${survey}">
					<li>&rarr; <a
						href="${createLink(controller:'admin',action:'objective',params:[surveyId: survey.id])}"><g:i18n
								field="${survey.names}" />
					</a>
					</li>
					<g:if test="${objective}">
						<li>&rarr; <a
							href="${createLink(controller:'admin', action:'subobjective',params:[surveyId: survey.id, objectiveId: objective.id])}">
								<g:i18n field="${objective.names}" />
						</a></li>
					</g:if>
					<g:if test="${subobjective}">
						<li>&rarr; <a
							href="${createLink(controller:'admin', action:'question',params:[surveyId: survey.id, objectiveId: subobjective.objective.id,subObjectiveId: subobjective.id])}">
								<g:i18n field="${subobjective.names}" /> </a>
						</li>
					</g:if>
					</g:if>
				</ul>
				<div class="clear"></div>
			</div>
		<div id="bottom-container" class="box">
			<div id="survey-admin-list-container">
				<!-- Template goes here -->

				<g:if test="${surveys || surveys?.size()==0}">
					<g:set var="template" value="surveyList" />
				</g:if>
				<g:if test="${objectives || survey}">
					<g:set var="template" value="objectiveList" />
				</g:if>
				<g:if test="${subobjectives || objective}">
					<g:set var="template" value="subobjectiveList" />
				</g:if>
				<g:if test="${questions || subobjective}">
					<g:set var="template" value="questionList" />
				</g:if>

				<g:render template="/survey/admin/${template}"
					model="[surveyId: survey, objectiveId: objective,subobjectiveId: subobjective]" />
				<!-- End of template -->
				<div class="clear"></div>
			</div>
			<div class="hidden flow-container"></div>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
	</div>

	<script type="text/javascript">
		$(document).ready(function() {
			$('#survey-admin-list-container').flow({
				onSuccess : function(data) {
					if (data.result == 'success') {
						location.reload();
					}
				}
			});
		});
	</script>
</body>
</html>