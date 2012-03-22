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
				<a href="${createLink(controller: 'planning', action:'list')}"><g:message code="planning.label"/></a>
			</li>
			<g:if test="${planning}">
				<li>
					&rarr; 
					<a href="${createLink(controller: 'planningType', action:'list', params:[planning: planning.id])}">
						<g:i18n field="${planning.names}" />
					</a>
				</li>
			</g:if>
		</ul>
		<div class="clear"></div>
	</div>
		
	<g:render template="/templates/genericList" model="[entityName: entityName, template: '/planning/admin/'+template]"/>
</body>
</html>