<g:set var="enteredQuestion" value="${surveyPage.questions[question]}"/>
<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div id="question-${question.id}" class="question question-table" data-question="${question.id}">
	<g:i18n field="${question.names}" />
	<div class="question-table-container">
		<table>
			<thead>
				<tr>
					<th class="question-tab-title-name"><g:i18n field="${question.tableNames}" /></th>
				    <g:set var="j" value="${0}"/>
					<g:each in="${question.getColumns(organisationUnitGroup)}" var="column">
						<g:set var="j" value="${j++}"/>
						<th class="${question.getColumns(organisationUnitGroup).size()!=j?'question-tab-title':''}">
							<g:i18n field="${column.names}" />
						</th>
					</g:each>
				</tr>
			</thead>
			<tbody>
				<g:set var="i" value="${0}"/>
				<g:each in="${question.getRows(organisationUnitGroup)}" var="row">
					<g:set var="i" value="${i+1}"/>
					<tr class="${i%2==0?'oddrow':'evenrow'}">
						<td><g:i18n field="${row.names}" /></td>
						<g:each in="${question.getColumns(organisationUnitGroup)}" var="column">
							<g:set var="surveyElement" value="${row.surveyElements[column]}"/>
							<g:set var="dataElement" value="${surveyElement.dataElement}"/>
							
							<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />
			
							<td id="element-${surveyElement.id}" class="survey-element">
								<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
								<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
								
								<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}" model="[
									value: enteredValue.value, 
									lastValue: enteredValue.lastValue,
									type: dataElement.type, 
									suffix:'',
									surveyElement: surveyElement, 
									enteredValue: enteredValue, 
									readonly: readonly
								]" />
							</td>
						</g:each>
					</tr>
				</g:each>
			</tbody>
		</table>
	</div>
</div>