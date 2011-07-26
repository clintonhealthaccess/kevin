<div>
	<g:i18n field="${question.names}" />
	<table class="question-table" id="question-table-${question.id}">
	<tr>
	<th><g:i18n field="${question.descriptions}" /></th>
	    <g:set var="j" value="${0}"/>
		<g:each in="${question.getColumns(organisationUnitGroup)}" var="column">
		<g:set var="j" value="${j++}"/>
			<th class="${question.getColumns(organisationUnitGroup).size()!=j?'question-tab-title':''}">
			<g:i18n field="${column.names}" />
			</th>
		</g:each>
	</tr>
	<g:set var="i" value="${0}"/>
	<g:each in="${question.getRows(organisationUnitGroup)}" var="row">
	<g:set var="i" value="${i+1}"/>
		<tr class="${i%2==0?'oddrow':'evenrow'}">
		<td><g:i18n field="${row.names}" /></td>
		<g:each in="${question.getColumns(organisationUnitGroup)}" var="column">
			<g:set var="surveyElement" value="${row.surveyElements[column]}"/>
			<g:set var="dataElement" value="${surveyElement.dataElement}"/>
			<td>
				<g:render template="/survey/element/${dataElement.type}" model="[surveyElementValue: surveyElementValues[surveyElement.id], readonly: readonly]" />
			</td>
		</g:each>
		</tr>
	</g:each>
	
	</table>
</div>