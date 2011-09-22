<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<g:set var="entityName" value="${message(code: code, default: 'Entity')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
	
	<r:require modules="form,fieldselection,cluetip,dropdown"/>
</head>
<body>
	<g:render template="/templates/genericList" model="[entityName: entityName, template: '/entity/'+template]"/>
</body>
</html>