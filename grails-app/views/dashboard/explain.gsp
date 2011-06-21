<%@ page import="org.chai.kevin.dashboard.DashboardPercentage.Status" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'dashboard.explanation.label', default: 'Dashboard explanation')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
		<div id="dashboard-explanation">
			<h3><g:i18n field="${explanation.entry.names}"/> in ${explanation.organisation.name}</h3>
			<g:if test="${explanation.info != null}">
				<g:render template="${explanation.info.template}" model="[info: explanation.info, groups: groups]"/>
			</g:if>
			<g:else>
				<div class="red bold">No expression available for this facility type.</div>
			</g:else>
		</div>
    </body>
</html>