<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<g:set var="entityName" value="${message(code: code)}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
	
	<!-- for admin forms -->
	<r:require modules="richeditor,datepicker,list"/>
	
</head>
<body>
	<div class="heading">
		<ul class="inline-list">
			<li>
				<a href="${createLink(controller: 'survey', action:'list')}"><g:message code="survey.label"/></a>
			</li>
			<g:if test="${survey}">
				<li>
					&rarr; 
					<a href="${createLink(controller: 'program', action:'list', params:['survey.id': survey.id])}">
						<g:i18n field="${survey.names}" />
					</a>
				</li>
			</g:if>
			<g:if test="${program}">
				<li>
					&rarr; 
					<a href="${createLink(controller: 'section', action:'list', params:['program.id': program.id])}">
						<g:i18n field="${program.names}" />
					</a>
				</li>
			</g:if>
			<g:if test="${section}">
				<li>
					&rarr; 
					<a href="${createLink(controller: 'question', action:'list', params:['section.id': section.id])}">
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