<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="surveyAdminPage.view.label"
		default="District Health System Portal" />
</title>
</head>
<body>
	<div id="survey-admin">
		<g:if test="${survey!=null}">
			<div id="top-container" class="box">
				<ul class="survey-admin-menu-list">
					<li>Survey: <a href="${createLink(controller:'admin', action:'survey')}">
					<g:i18n field="${survey.names}" /></a>
					</li>
					<g:if test="${objective!=null}">
						<li>Strategic Objective: <a href="${createLink(controller:'admin', action:'objective',params:[survey: survey.id])}">
						<g:i18n field="${objective.names}" /></a>
						</li>
					</g:if>
					<g:if test="${section!=null}">
						<li>Section: <a href="${createLink(controller:'admin', action:'section',params:[objective: objective.id])}"><g:i18n
								field="${section.names}" />
								</a></li>
					</g:if>
				</ul>
				<div class="clear"></div>
			</div>
		</g:if>
		<div id="bottom-container" class="box">
			<div id="survey-admin-list-container">
				<!-- Template goes here -->
				
				<g:if test="${survey==null}">
				<g:render template="/survey/admin/surveylist"  model="[]" /> 
				</g:if>
				<g:if test="${survey!=null && objective==null}">
				<g:render template="/survey/admin/objectivelist" model="[survey: survey]" /> 
				</g:if>
				<g:if test="${survey!=null && objective!=null && section == null}">
				<g:render template="/survey/admin/sectionlist" model="[survey: survey, objective: objective]" /> 
				</g:if>
				<g:if test="${survey!=null && objective!=null && section != null}">
				<g:render template="/survey/admin/questionlist" model="[survey: survey, objective: objective,section: section]" /> 
				</g:if>
				
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