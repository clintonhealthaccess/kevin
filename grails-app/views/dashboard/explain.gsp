<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'dashboard.explanation.label')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
		<div class="explanation">
			<h3><g:i18n field="${dashboardEntity.names}"/> in ${info.location.name}</h3>
			<g:if test="${info != null}">
				<g:render template="${info.template}" model="[info: info, types: types]"/>
			</g:if>
			<g:else>
				<div class="red bold"><g:message code="dashboard.explanation.noexpression"/></div>
			</g:else>
		</div>
    </body>
</html>