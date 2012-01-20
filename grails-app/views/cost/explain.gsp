<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'cost.explanation.label', default: 'Cost explanation')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
    	<div class="explanation-heading">
    		Applies to:
    		<g:each in="${explanation.types}" var="group" status="i">
    			<span>${group.name}</span><g:if test="${i < explanation.types.size() - 1}">,</g:if>
    		</g:each>
    	</div>
    	<table class="listing">
			<g:each in="${explanation.locations}" var="location">
				<tr>
					<td>
						<span>
							<g:if test="${false}">
								<a href="${createLink(controller:'cost', action:'view', params:[period: explanation.currentPeriod.id, objective: explanation.currentTarget.id, location: location.id])}">${location.name}</a>
							</g:if>
							<g:else>
								${location.name}
							</g:else>
						</span>
					</th>
					<g:each in="${explanation.years}" var="year">
						<td><span>${explanation.getCost(location, year).roundedValue}</span></td>
					</g:each>
				</tr>
			</g:each>
		</table>
		
    </body>
</html>