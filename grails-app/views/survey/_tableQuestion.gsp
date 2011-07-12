<g:i18n field="${question.names}" />
<span class="display-in-block">
<table class="question-table" id="question-table-${question.id}">
<tr>
<th><g:i18n field="${question.descriptions}" /></th>
    <g:set var="j" value="${0}"/>
	<g:each in="${question.columns}" var="column">
	<g:set var="j" value="${j++}"/>
		<th class="${question.columns.size()!=j?'question-tab-title':''}">
		<g:i18n field="${column.names}" />
		</th>
	</g:each>
</tr>
<g:set var="i" value="${0}"/>
<g:each in="${question.rows}" var="row">
<g:set var="i" value="${i+1}"/>
	<tr class="${i%2==0?'oddrow':'evenrow'}">
	<td><g:i18n field="${row.names}" /></td>
	<g:each in="${row.columns}" var="column">
		<g:set var="surveyElement" value="${row.surveyElements[column]}"/>
		<g:set var="dataElement" value="${surveyElement.dataElement}"/>
		<td>
			<g:render template="/survey/${dataElement.type}" model="[surveyElementValue: surveyElementValues[surveyElement.id]]" />
		</td>
	</g:each>
	</tr>
</g:each>

</table>
</span>