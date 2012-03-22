<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<g:set var="entityName" value="${message(code: code)}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
	
	<r:require modules="list"/>
</head>
<body>
	<g:render template="/templates/genericList" model="[entityName: entityName, template: '/entity/'+template]"/>
</body>
</html>