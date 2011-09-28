<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'dashboard.explanation.label', default: 'Data element explanation')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    
    <body>

		<div class="box">
			<div><g:i18n field="${dataElement.names}"/></div>
			<div class="row">Type: <span class="type"><g:toHtml value="${dataElement.type.getDisplayedValue(2, null)}"/></span></div>
			<div><g:i18n field="${dataElement.descriptions}"/></div>
			<div class="clear"></div>
		</div>
		
		<div>
			<g:if test="${surveyElements.size()!=0}">
				<table class="listing">
					<thead>
						<tr>
							<th>Iteration</th>
							<th>Survey</th>
							<th>Question</th>
							<th>Total Number of OrgUnit Applicable</th>
						</tr>
					</thead>
					<tbod>
						<g:each in="${surveyElements}" status="i" var="surveyElement"> 
							<g:set var="question" value="${surveyElement.key.surveyQuestion}" /> 
							<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								<td>${question.section.objective.survey.period.startDate} &harr; ${question.section.objective.survey.period.endDate}</td>
								<td>${i18n(field:question.section.objective.survey.names)}</td>
								<td>${question.getString(i18n(field: question.names).toString(),100)} </a></td>
								<td>${surveyElement.value}</td>
							</tr>
						</g:each>
					</tbod>
				</table>
			</g:if>
			<g:else>
				No Survey Element Associated to This Data Element
			</g:else>
			<div class="clear"></div>
		</div>
		
		<div>
			<table class="listing">
				<thead>
					<tr>
						<th>Iteration</th>
						<th>Number of Data Value</th>
					</tr>
				</thead>
				<tbody>
					<g:each in="${periodValues}" status="i" var="periodValue"> 
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td>${periodValue.key.startDate} &harr; ${periodValue.key.endDate}</td>
							<td>${periodValue.value}</td>
						</tr>
					</g:each>
				</tbody>
			</table>
			<div class="clear"></div>
		</div>
	</body>
</html>

