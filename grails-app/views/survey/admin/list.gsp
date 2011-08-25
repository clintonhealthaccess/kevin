<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<g:set var="entityName" value="${message(code: code, default: 'Entity')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	
	<div class="breadcrumbs">
		<ul>
			<li>
				<a href="${createLink(controller: 'survey', action:'list')}">Surveys</a>
			</li>
			<g:if test="${survey}">
				<li>
					&rarr; 
					<a href="${createLink(controller: 'objective', action:'list', params:[surveyId: survey.id])}">
						<g:i18n field="${survey.names}" />
					</a>
				</li>
			</g:if>
			<g:if test="${objective}">
				<li>
					&rarr; 
					<a href="${createLink(controller: 'section', action:'list', params:[surveyId: survey.id, objectiveId: objective.id])}">
						<g:i18n field="${objective.names}" />
					</a>
				</li>
			</g:if>
			<g:if test="${section}">
				<li>
					&rarr; 
					<a href="${createLink(controller: 'question', action:'list', params:[surveyId: survey.id, objectiveId: section.objective.id, sectionId: section.id])}">
						<g:i18n field="${section.names}" /> 
					</a>
				</li>
			</g:if>
		</ul>
		<div class="clear"></div>
	</div>
		
	<g:render template="/templates/genericList" model="[entityName: entityName, template: '/survey/admin/'+template]"/>
</body>
</html>