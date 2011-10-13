<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<g:set var="entityName" value="${message(code: code, default: 'Entity')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
	
	<r:require modules="chosen,richeditor,fieldselection,cluetip,form,dropdown,datepicker,list"/>
</head>
<body>
	<g:render template="${template}"/>
</body>
</html>