<%@ page import="org.chai.kevin.dashboard.DashboardPercentage.Status" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'maps.explanation.label', default: 'Maps explanation')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
		
		<h5><g:i18n field="${explanation.mapsTarget.names}"/> in ${explanation.organisation.name}</h5>
		
		
		<div class="float-left">${explanation.value}</div>
    </body>
</html>