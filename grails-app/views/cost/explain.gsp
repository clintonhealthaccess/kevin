<%@ page import="org.chai.kevin.dashboard.DashboardPercentage.Status" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'cost.explanation.label', default: 'Cost explanation')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
    	<table>
			<g:each in="${explanation.organisations}" var="organisation">
				<tr>
					<th>
						<span>
							<g:if test="${false}">
								<a href="${createLink(controller:'cost', action:'view', params:[period: explanation.currentPeriod.id, objective: explanation.currentTarget.id, organisation: organisation.id])}">${organisation.name}</a>
							</g:if>
							<g:else>
								${organisation.name}
							</g:else>
						</span>
					</th>
					<g:each in="${explanation.years}" var="year">
						<td><span>${explanation.getCost(organisation, year).roundedValue}</span></td>
					</g:each>
				</tr>
			</g:each>
		</table>
		
    </body>
</html>