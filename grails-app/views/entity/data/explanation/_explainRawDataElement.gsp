<%@page import="org.chai.kevin.util.DataUtils"%>

<ul class="tab-subnav horizontal">
	<li>
		<a class="js_tab-selector selected" href="#" data-type="reports" data-id="${dataElement.id}">Used in reports</a>
	</li>
	<li>
		<a class="js_tab-selector" href="#" data-type="survey" data-id="${dataElement.id}">Used in survey</a>
	</li>
	<!-- li>
		<a class="js_tab-selector" href="#" data-type="planning" data-id="${dataElement.id}">Used in planning</a>
	</li -->
	<li>
		<a class="js_tab-selector" href="#" data-type="data" data-id="${dataElement.id}">Used in data</a>
	</li>
</ul>

<div class="js_tab-${dataElement.id}" id="js_tab-reports-${dataElement.id}">
	<g:render template="/entity/data/explanation/referencingReportTargets" model="[referencingTargets: referencingTargets]"/>
</div>

<div class="js_tab-${dataElement.id} hidden" id="js_tab-survey-${dataElement.id}">
	<g:if test="${!surveyElements.empty}">
		<table class="listing">
			<thead>
				<tr>
					<th><g:message code="period.label"/></th>
					<th><g:message code="survey.label"/></th>
					<th><g:message code="survey.question.label"/></th>
					<th><g:message code="rawdataelement.surveyelement.location.applicable.label"/></th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${surveyElements}" status="i" var="surveyElement"> 
					<g:set var="question" value="${surveyElement.key.surveyQuestion}" /> 
					<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
						<td>${DataUtils.formatDate(question.section.program.survey.period.startDate)} &harr; ${DataUtils.formatDate(question.section.program.survey.period.endDate)}</td>
						<td>${i18n(field:question.section.program.survey.names)}</td>
						<td><g:stripHtml field="${i18n(field: question.names)}" chars="100"/></a></td>
						<td>${surveyElement.value}</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</g:if>
	<g:else>
		<div class="explanation-empty">
			<g:message code="rawdataelement.surveyelement.notassociated"/>
		</div>
	</g:else>
</div>

<div class="js_tab-${dataElement.id} hidden" id="js_tab-data-${dataElement.id}">
	<g:render template="/entity/data/explanation/referencingData" model="[referencingData: referencingData]"/>
</div>